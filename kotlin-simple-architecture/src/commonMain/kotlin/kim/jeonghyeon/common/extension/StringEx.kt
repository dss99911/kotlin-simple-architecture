@file:Suppress("unused")

package kim.jeonghyeon.common.extension

fun String?.toDouble(defValue: Double): Double = try {
    this?.toDouble() ?: 0.0
} catch (e: NumberFormatException) {
    defValue
}

inline fun CharSequence?.isNotNullNotEmpty(): Boolean = this != null && length > 0
