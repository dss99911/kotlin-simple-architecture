@file:Suppress("unused")

package kim.jeonghyeon.jvm.extension

import java.text.SimpleDateFormat
import java.util.*

//todo delete?
fun calendar(milliseconds: Long): Calendar =
    Calendar.getInstance().apply { timeInMillis = milliseconds }

fun Calendar.getyMd(): YearMonthDay {
    val year = Calendar.getInstance().get(Calendar.YEAR)
    val month = Calendar.getInstance().get(Calendar.MONTH)
    val date = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

    return YearMonthDay(year, month, date)
}

fun Calendar.addDate(date: Int): Calendar {
    add(Calendar.DAY_OF_MONTH, date)
    return this
}

fun Calendar.setDayOfWeek(day: Int): Calendar {
    set(Calendar.DAY_OF_WEEK, day)
    return this
}

fun Calendar.setHour(hour: Int): Calendar {
    set(Calendar.HOUR_OF_DAY, hour)
    return this
}

fun Calendar.setMinute(minute: Int): Calendar {
    set(Calendar.MINUTE, minute)
    return this
}

fun Calendar.setSecond(second: Int): Calendar {
    set(Calendar.SECOND, second)
    return this
}

fun Calendar.format(pattern: String): String =
    SimpleDateFormat(pattern, Locale.getDefault()).format(this.time)

data class YearMonthDay(val year: Int, val month: Int, val day: Int)