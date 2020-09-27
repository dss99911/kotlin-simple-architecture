@file:Suppress("unused")

package kim.jeonghyeon.jvm.extension

import com.google.gson.Gson
import com.google.gson.JsonObject
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

//todo delete?
fun String.toDate(formatText: String): Date? {
    val format = SimpleDateFormat(formatText, Locale.getDefault())
    return format.parse(this)
}

fun String.toCalendar(pattern: String): Calendar? = toDate(pattern)
    ?.let { Calendar.getInstance().apply { time = it } }

fun <T> String.toJsonObject(clazz: Class<T>): T = Gson().fromJson(this, clazz)
inline fun <reified T> String.toJsonObject(): T = Gson().fromJson(this, T::class.java)
fun <T> String.toJsonObject(type: Type): T = Gson().fromJson(this, type)
fun String.toJsonObject(): JsonObject = Gson().fromJson(this, JsonObject::class.java)
