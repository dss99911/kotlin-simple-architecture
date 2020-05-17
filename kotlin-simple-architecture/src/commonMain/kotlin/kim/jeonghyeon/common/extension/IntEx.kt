@file:Suppress("unused")

package kim.jeonghyeon.common.extension

fun Int.loopBreak(action: () -> Boolean) {
    var count = this
    while (count > 0) {
        if (!action()) {
            break
        }
        count--
    }
}

fun Int?.plus(value: String?): String? = toString() + value