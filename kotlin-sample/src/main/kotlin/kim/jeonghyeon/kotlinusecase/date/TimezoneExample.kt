package kim.jeonghyeon.kotlinusecase.date

import java.text.SimpleDateFormat
import java.util.*

class TimezoneExample {
    fun timezone() {
        //parse date string from a certain timezone
        //Date class contains utc+0 time value. and show with default timezone of the system.
        //so, if date string is from JST. and system is IST. it convert JST to UTC+0 and convert UTC+0 to IST
        //so, time value(long value) doesn't contains timezone value. it just indicate UTC+0
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-ddhh:mm:ss", Locale.ENGLISH)
        simpleDateFormat.timeZone = TimeZone.getTimeZone("JST")
        simpleDateFormat.parse("2018-12-1011:01:20").time
    }
}