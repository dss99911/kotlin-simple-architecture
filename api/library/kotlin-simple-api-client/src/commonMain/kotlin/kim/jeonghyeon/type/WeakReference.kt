package kim.jeonghyeon.type

expect class WeakReference<T : Any>(referred: T) {
    fun clear()
    fun get(): T?
}