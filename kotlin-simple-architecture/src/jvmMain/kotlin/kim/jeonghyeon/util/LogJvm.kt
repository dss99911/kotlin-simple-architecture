package kim.jeonghyeon.util

import com.google.gson.Gson
import io.ktor.util.*
import kim.jeonghyeon.di.logger

actual class Logger actual constructor() {
    actual inline fun i(message: String) {
        logger.info(message)
    }
    actual inline fun i(vararg obj: Any?) {
        logger.info(Gson().toJson(obj))
    }
    actual inline fun d(message: String) {
        logger.debug(message)
    }
    actual inline fun d(vararg obj: Any?) {
        logger.debug(Gson().toJson(obj))
    }
    actual inline fun e(e: Throwable) {
        logger.error(e)
    }
    actual inline fun e(e: String) {
        logger.error(e)
    }
    actual inline fun e(vararg obj: Any?) {
        logger.error(Gson().toJson(obj))
    }
}