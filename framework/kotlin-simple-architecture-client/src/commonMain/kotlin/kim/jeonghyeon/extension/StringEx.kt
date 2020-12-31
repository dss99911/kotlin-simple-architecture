@file:Suppress("unused")

package kim.jeonghyeon.extension

import io.ktor.client.features.json.serializer.*
import io.ktor.http.content.*
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

fun CharSequence?.isNotEmpty(): Boolean = this != null && !isEmpty()

fun String.replaceLast(oldValue: String, newValue: String, ignoreCase: Boolean = false): String {
    val index = lastIndexOf(oldValue, ignoreCase = ignoreCase)
    return if (index < 0) this else replaceRange(index, index + oldValue.length, newValue)
}


inline fun <reified T> T.toJsonString(): String {
//    return Json { }.encodeToString(this)
    return (KotlinxSerializer().write(this?: return "null") as TextContent).text
}

inline fun <reified T : Any?> String.fromJsonString(): T {
    return Json { ignoreUnknownKeys = true }.decodeFromString(this)
}