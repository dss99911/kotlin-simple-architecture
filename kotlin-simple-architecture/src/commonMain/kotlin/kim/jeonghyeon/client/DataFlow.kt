package kim.jeonghyeon.client

import kim.jeonghyeon.type.atomic
import kim.jeonghyeon.util.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * as [MutableStateFlow] is Interface,
 * xcode recognize value as Any.
 * if it's class, it's recognize as defined type
 *
 * the reason to wrap MutableStateFlow with StateFlowImpl
 * - StateFlow is distinct. so, if same value is set, collector deosn't receive data.
 * - There are two cases on view side.
 *      - event and state
 *      - event is like navigation. it should be called whenever value is set(but StateFlow doesn't emit if data is same)
 *      - state is like setting data on composable, it's not required to update if data is same.
 * - one data can be used as event and state both.
 * - so, it should be decided on view side whether it's event or state
 * - so, [collectDistinct] is added for state
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DataFlow<T>(value: T) : MutableStateFlow<T> by StateFlowImpl(value) {
    /**
     * used in ios
     */
    fun watch(scope: CoroutineScope, perform: (T) -> Unit) {
        scope.launch {
            collect {
                perform(it)
            }
        }
    }

    fun watchDistinct(scope: CoroutineScope, perform: (T) -> Unit) {
        scope.launch {
            collectDistinct {
                perform(it)
            }
        }
    }

    suspend fun collectDistinct(collector: FlowCollector<T>) {
        var prevState: Any? = NONE
        collect { newState ->
            log.i("old($prevState) -> new($newState)")
            if (prevState != newState) {
                collector.emit(newState)
                prevState = newState
            }
        }
    }

    suspend inline fun collectDistinct(crossinline action: suspend (T) -> Unit) {
        collectDistinct(object : FlowCollector<T> {
            override suspend fun emit(value: T) {
                action(value)
            }
        })
    }

    object NONE
}

suspend fun <T : Any> DataFlow<T?>.collectNotNull(onCollect: (T) -> Unit) {
    collect {
        if (it != null) {
            onCollect(it)
        }
    }
}

suspend fun <T : Any> DataFlow<T?>.collectDistinctNotNull(onCollect: (T) -> Unit) {
    collectDistinct {
        if (it != null) {
            onCollect(it)
        }
    }
}

fun DataFlow<Unit?>.call() {
    value = Unit
}

fun <T> DataFlow<T>.call(value: T) {
    this.value = value
}

@OptIn(ExperimentalCoroutinesApi::class)
private class StateFlowImpl<T>(initialValue: T) : MutableStateFlow<T> {
    private val flow = MutableStateFlow(0)
    private val _state = atomic(initialValue)

    override var value: T
        get() = _state.value
        set(value) {
            _state.value = value
            flow.value += 1
        }

    @OptIn(InternalCoroutinesApi::class)
    override suspend fun collect(collector: FlowCollector<T>) {
        flow.collect {
            collector.emit(value)
        }
    }
}
