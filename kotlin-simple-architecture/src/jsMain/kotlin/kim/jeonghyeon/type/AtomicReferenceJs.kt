package kim.jeonghyeon.type

import kotlinx.atomicfu.atomic

actual class AtomicReference<T> actual constructor(initial: T) {
    private val atomic = atomic(initial)

    actual fun getAndSet(value: T): T = atomic.getAndSet(value)
    actual var value: T
        get() {
            return atomic.value
        }
        set(value) {
            atomic.value = value
        }
}
