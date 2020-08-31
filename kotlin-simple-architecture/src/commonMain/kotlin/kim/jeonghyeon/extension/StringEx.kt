@file:Suppress("unused")

package kim.jeonghyeon.extension

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun String?.toDouble(defValue: Double): Double = try {
    this?.toDouble() ?: 0.0
} catch (e: NumberFormatException) {
    defValue
}

fun String.equalsAny(vararg string: String): Boolean = string.any { it == this@equalsAny }

fun String.equalsAll(vararg string: String): Boolean = string.all { it == this@equalsAll }

inline fun CharSequence?.isNotEmpty(): Boolean = this != null && !isEmpty()

fun String.replaceLast(oldValue: String, newValue: String, ignoreCase: Boolean = false): String {
    val index = lastIndexOf(oldValue, ignoreCase = ignoreCase)
    return if (index < 0) this else replaceRange(index, index + oldValue.length, newValue)
}

inline fun <reified T : Any> T.toJsonString(): String {
    return Json { }.encodeToString(this)
}

inline fun <reified T : Any> String.fromJsonString(): T {
    return Json { }.decodeFromString(this)
}