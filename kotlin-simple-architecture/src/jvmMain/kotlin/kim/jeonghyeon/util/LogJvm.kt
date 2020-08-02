package kim.jeonghyeon.util

import com.google.gson.Gson

@Suppress("NOTHING_TO_INLINE")
actual inline fun log(e: Throwable) {
    println("L::" + e.message)
}

@Suppress("NOTHING_TO_INLINE")
actual inline fun log(message: String) {
    println("L::$message")
}

@Suppress("NOTHING_TO_INLINE")
actual inline fun log(vararg obj: Any?) {
    println("L::" + Gson().toJson(obj))
}