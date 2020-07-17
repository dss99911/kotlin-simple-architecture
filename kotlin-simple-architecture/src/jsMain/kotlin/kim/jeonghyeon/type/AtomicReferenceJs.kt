package kim.jeonghyeon.type

import kotlinx.atomicfu.atomic

actual class AtomicReference<T> actual constructor(initial: T) {
    val atomic = atomic(initial)

    actual fun getAndSet(value: T): T = atomic.getAndSet(value)
}