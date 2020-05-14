@file:Suppress("unused")

package kim.jeonghyeon.kotlinlibrary.extension

inline fun <T : Boolean?> T.onTrue(action: () -> Unit): T {
    if (this == true) {
        action()
    }

    return this
}

inline fun <T : Boolean?> T.onFalse(action: () -> Unit): T {
    if (this == false) {
        action()
    }

    return this
}

inline fun <T : Boolean?> T.onNullOrFalse(action: () -> Unit): T {
    if (this === null || !this) {
        action()
    }

    return this
}