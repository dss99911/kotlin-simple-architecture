package kim.jeonghyeon.type



actual class AtomicReference<T> actual constructor(initial: T) {

    private val atomic = java.util.concurrent.atomic.AtomicReference(initial)

    actual fun getAndSet(value: T): T = atomic.getAndSet(value)
    actual var value: T
        get() {
            return atomic.get()
        }
        set(value) {
            atomic.set(value)
        }
}