package kim.jeonghyeon.util

import com.google.gson.Gson
import kim.jeonghyeon.androidlibrary.extension.isDebug
import kim.jeonghyeon.androidlibrary.extension.isTesting
import timber.log.Timber

@Suppress("NOTHING_TO_INLINE")
actual inline fun log(e: Throwable) {
    if (isDebug) {
        if (isTesting) {
            e.printStackTrace()
        } else {
            Timber.e(e)
        }
    }
}

@Suppress("NOTHING_TO_INLINE")
actual inline fun log(message: String) {
    if (isDebug) {
        if (isTesting) {
            println(message)
        } else {
            Timber.v(message)
        }
    }
}

@Suppress("NOTHING_TO_INLINE")
actual inline fun log(vararg obj: Any?) {
    if (isDebug) {
        if (isTesting) {
            println(Gson().toJson(obj))
        } else {
            Timber.v(Gson().toJson(obj))
        }
    }
}