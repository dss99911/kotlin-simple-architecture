@file:Suppress("unused")

package kim.jeonghyeon.kotlinlibrary.extension

/**
 * use repeat()
 */
//fun Int.loop(action: () -> Unit) {
//    var count = this
//    while (count > 0) {
//        action()
//        count--
//    }
//}

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