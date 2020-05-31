package kim.jeonghyeon.type

actual class WeakReference<T : Any> actual constructor(referred: T) {
    var data: T? = referred
    actual fun clear() {
        data = null
    }

    actual fun get(): T? {
        return data
    }
}