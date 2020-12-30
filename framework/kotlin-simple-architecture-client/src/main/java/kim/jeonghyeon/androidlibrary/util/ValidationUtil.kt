package kim.jeonghyeon.androidlibrary.util

import java.util.regex.Pattern

object ValidationUtil {
    private val EMAIL_PATTERN by lazy {
        Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")
    }

    fun isValidEmail(email: String) = EMAIL_PATTERN.matcher(email).matches()
}