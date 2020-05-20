@file:Suppress("unused")

package kim.jeonghyeon.common.extension

/**
 * [1,2,3,4,5]
 * split(2)
 * [[1,2],[3,4],[5]]
 */
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

/**
 * [newList] should not be empty
 */
inline fun <T> List<T>?.notEmpty(newList: () -> List<T>) =
    if (this === null || this.isEmpty()) {
        newList().also {
            check(it.isNotEmpty()) { "list is empty" }
        }
    } else this

fun <E> MutableList<E>.removeLast(): E? = if (size == 0) null else removeAt(size - 1)