package kim.jeonghyeon.androidlibrary.architecture.livedata

/**
 * whenever add item or change item. no need to setValue again for observer to observe
 */
typealias LiveList<T> = LiveObject<List<T>>

inline fun <reified T> LiveList<T>.add(item: T) {
    val value = this.value

    val newValue = if (value == null) {
        listOf(item)
    } else {
        listOf(*value.toTypedArray(), item)
    }

    this.value = newValue
}