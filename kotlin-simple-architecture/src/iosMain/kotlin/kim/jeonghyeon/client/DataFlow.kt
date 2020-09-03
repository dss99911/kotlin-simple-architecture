package kim.jeonghyeon.client

import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.loop
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * as [MutableStateFlow] is Interface,
 * xcode recognize value as Any.
 * if it's class, it's recognize as defined type
 */
//todo after fix https://github.com/Kotlin/kotlinx.coroutines/issues/2226
// delete this
actual class DataFlow<T> actual constructor(value: T) : MutableStateFlow<T> by DataFlowImpl<T>(value ?: NULL2)

class DataFlowImpl<T>(initialValue: Any) : SynchronizedObject(), MutableStateFlow<T> {
    private val _state = kotlinx.atomicfu.atomic(initialValue) // T | NULL
    private val sequence = kotlinx.atomicfu.atomic(0) // serializes updates, value update is in process when sequence is odd
    private val slots = kotlinx.atomicfu.atomic(arrayOfNulls<StateFlowSlot?>(2))
    private val nSlots = kotlinx.atomicfu.atomic(0) // T | NULL // number of allocated (!free) slots
    private val nextIndex = kotlinx.atomicfu.atomic(0) // oracle for the next free slot index

    @OptIn(ExperimentalCoroutinesApi::class)
    @Suppress("UNCHECKED_CAST")
    public override var value: T
        get() = NULL2.unbox(_state.value)
        set(value) {
            var curSequence = 0
            var curSlots: Array<StateFlowSlot?> = this.slots.value // benign race, we will not use it
            val newState = value ?: NULL2
            kotlinx.atomicfu.locks.synchronized(this) {
                val oldState = _state.value
                if (oldState == newState) {
                    return
                } // Don't do anything if value is not changing

                _state.value = newState
                curSequence = sequence.value
                if (curSequence and 1 == 0) { // even sequence means quiescent state flow (no ongoing update)
                    curSequence++ // make it odd
                    sequence.value = curSequence
                } else {
                    // update is already in process, notify it, and return
                    sequence.value = curSequence + 2 // change sequence to notify, keep it odd
                    return
                }
                curSlots = slots.value // read current reference to collectors under lock
            }
            /*
                   Fire value updates outside of the lock to avoid deadlocks with unconfined coroutines
                   Loop until we're done firing all the changes. This is sort of simple flat combining that
                   ensures sequential firing of concurrent updates and avoids the storm of collector resumes
                   when updates happen concurrently from many threads.
                 */
            while (true) {
                // Benign race on element read from array
                for (col in curSlots) {
                    col?.makePending()
                }
                // check if the value was updated again while we were updating the old one
                kotlinx.atomicfu.locks.synchronized(this) {
                    if (sequence.value == curSequence) { // nothing changed, we are done
                        sequence.value = curSequence + 1 // make sequence even again
                        return // done
                    }
                    // reread everything for the next loop under the lock
                    curSequence = sequence.value
                    curSlots = slots.value
                }
            }
        }

    @OptIn(InternalCoroutinesApi::class)
    override suspend fun collect(collector: FlowCollector<T>) {
        val slot = allocateSlot()
        var prevState: Any? = null // previously emitted T!! | NULL (null -- nothing emitted yet)
        try {
            // The loop is arranged so that it starts delivering current value without waiting first
            while (true) {
                // Here the coroutine could have waited for a while to be dispatched,
                // so we use the most recent state here to ensure the best possible conflation of stale values
                val newState = _state.value
                // Conflate value emissions using equality
                if (prevState == null || newState != prevState) {
                    collector.emit(NULL2.unbox(newState))
                    prevState = newState
                }
                // Note: if awaitPending is cancelled, then it bails out of this loop and calls freeSlot
                if (!slot.takePending()) { // try fast-path without suspending first
                    slot.awaitPending() // only suspend for new values when needed
                }
            }
        } finally {
            freeSlot(slot)
        }
    }

    private fun allocateSlot(): StateFlowSlot = kotlinx.atomicfu.locks.synchronized(this) {
        val size = slots.value.size
        if (nSlots.value >= size) slots.value = slots.value.copyOf(2 * size)
        var index = nextIndex.value
        var slot: StateFlowSlot
        while (true) {
            slot = slots.value[index] ?: StateFlowSlot().also { slots.value[index] = it }
            index++
            if (index >= slots.value.size) index = 0
            if (slot.allocate()) break // break when found and allocated free slot
        }
        nextIndex.value = index
        nSlots.incrementAndGet()
        slot
    }

    private fun freeSlot(slot: StateFlowSlot): Unit = kotlinx.atomicfu.locks.synchronized(this) {
        slot.free()
        nSlots.decrementAndGet()
    }
}

class StateFlowSlot {
    /**
     * Each slot can have one of the following states:
     *
     * * `null` -- it is not used right now. Can [allocate] to new collector.
     * * `NONE` -- used by a collector, but neither suspended nor has pending value.
     * * `PENDING` -- pending to process new value.
     * * `CancellableContinuationImpl<Unit>` -- suspended waiting for new value.
     *
     * It is important that default `null` value is used, because there can be a race between allocation
     * of a new slot and trying to do [makePending] on this slot.
     */
    private val _state = kotlinx.atomicfu.atomic<Any?>(null)

    fun allocate(): Boolean {
        // No need for atomic check & update here, since allocated happens under StateFlow lock
        if (_state.value != null) return false // not free
        _state.value = NONE // allocated
        return true
    }

    fun free() {
        _state.value = null // free now
    }

    @Suppress("UNCHECKED_CAST")
    fun makePending() {
        _state.loop { state ->
            when {
                state == null -> return // this slot is free - skip it
                state === PENDING -> return // already pending, nothing to do
                state === NONE -> { // mark as pending
                    if (_state.compareAndSet(state, PENDING)) return
                }
                else -> { // must be a suspend continuation state
                    // we must still use CAS here since continuation may get cancelled and free the slot at any time
                    if (_state.compareAndSet(state, NONE)) {
                        (state as CancellableContinuation<Unit>).resume(Unit)
                        return
                    }
                }
            }
        }
    }

    fun takePending(): Boolean = _state.getAndSet(NONE)!!.let { state ->
        check(state !is CancellableContinuation<*>)
        return state === PENDING
    }

    @Suppress("UNCHECKED_CAST")
    suspend fun awaitPending(): Unit = suspendCancellableCoroutine sc@ { cont ->
        check(_state.value !is CancellableContinuation<*>) // can be NONE or PENDING
        if (_state.compareAndSet(NONE, cont)) return@sc // installed continuation, waiting for pending
        // CAS failed -- the only possible reason is that it is already in pending state now
        check(_state.value === PENDING)
        cont.resume(Unit)
    }
}

val NONE = Symbol("NONE")
val PENDING = Symbol("PENDING")
val NULL2 = Symbol("NULL")//error when using name NULL on swift
class Symbol(val symbol: String) {
    override fun toString(): String = symbol

    @Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
    inline fun <T> unbox(value: Any?): T = if (value === this) null as T else value as T
}