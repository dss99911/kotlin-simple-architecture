@file:Suppress("unused")

package kim.jeonghyeon.kotlinlibrary.extension

import com.google.gson.Gson
import com.google.gson.JsonObject
import java.text.SimpleDateFormat
import java.util.*

fun String.toCalendar(pattern: String): Calendar? = toDate(pattern)
    ?.let { Calendar.getInstance().apply { time = it } }

fun <T> String.toJsonObject(clazz: Class<T>): T = Gson().fromJson<T>(this, clazz)
fun String.toJsonObject(): JsonObject = Gson().fromJson<JsonObject>(this, JsonObject::class.java)
fun String.toDate(formatText: String): Date? {
    val format = SimpleDateFormat(formatText, Locale.getDefault())
    return format.parse(this)
}

fun String?.toDouble(defValue: Double): Double = try {
    this?.toDouble() ?: 0.0
} catch (e: NumberFormatException) {
    defValue
}