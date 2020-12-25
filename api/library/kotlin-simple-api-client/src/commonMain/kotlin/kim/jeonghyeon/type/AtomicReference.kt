package kim.jeonghyeon.type

//todo I couldn't refer AtomicRef on common. if it's possible this can be removed
expect class AtomicReference<T>(initial: T) {
    fun getAndSet(value: T): T
    var value: T
}

fun <T> atomic(initial: T) = AtomicReference(initial)