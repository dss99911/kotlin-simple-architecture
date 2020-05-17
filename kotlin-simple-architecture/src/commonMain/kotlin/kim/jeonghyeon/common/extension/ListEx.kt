@file:Suppress("unused")

package kim.jeonghyeon.common.extension

fun <T> List<T>.split(size: Int): List<List<T>> {
    var curIndex = 0
    val list = mutableListOf<List<T>>()

    while (curIndex < this.size) {
        var endIndex = curIndex + size
        if (endIndex > this.size) endIndex = this.size

        list.add(this.subList(curIndex, endIndex))

        curIndex = endIndex
    }

    return list
}

inline fun <T> List<T>?.onEmpty(newList: () -> List<T>) =
    if (this === null || this.isEmpty()) newList() else this

inline fun <T> List<T>?.onNotEmpty(action: (List<T>?) -> List<T>?) =
    if (this === null || this.isEmpty()) this else action(this)

fun <I, T : MutableCollection<I>> T.addAllIf(
    list: Collection<I>,
    filter: (item: I, list: T) -> Boolean
): T {
    addAll(list.filter { filter(it, this) })
    return this
}

fun <E> MutableList<E>.removeLast(): E? = if (size == 0) null else removeAt(size - 1)