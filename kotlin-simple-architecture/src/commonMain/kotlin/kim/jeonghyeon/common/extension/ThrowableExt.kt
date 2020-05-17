@file:Suppress("unused")

package kim.jeonghyeon.common.extension


inline fun <T> ignoreException(action: () -> T): T? = try {
    action()
} catch (e: Exception) {
    null
}

inline fun <T> ignoreException(defValue: T?, action: () -> T): T? = try {
    action()
} catch (e: Exception) {
    defValue
}