package kim.jeonghyeon.androidlibrary.util

import com.google.gson.Gson
import kim.jeonghyeon.androidlibrary.extension.app
import kim.jeonghyeon.androidlibrary.extension.isTesting
import timber.log.Timber

/**
 * recommended to wrap this function with if (BuildConfig.Debug) { log() }
 * if it's release, the code will be removed.
 */
object Logger {
    @Suppress("NOTHING_TO_INLINE")
    inline fun log(e: Throwable) {
        if (!app.isProd || app.isDebug) {
            if (isTesting) {
                e.printStackTrace()
            } else {
                Timber.e(e)
            }
        }
    }

    @Suppress("NOTHING_TO_INLINE")
    inline fun log(message: String) {
        if (!app.isProd || app.isDebug) {
            if (isTesting) {
                println(message)
            } else {
                Timber.v(message)
            }

        }
    }


    @Suppress("NOTHING_TO_INLINE")
    inline fun log(vararg obj: Any?) {
        if (!app.isProd || app.isDebug) {
            if (isTesting) {
                println(Gson().toJson(obj))
            } else {
                Timber.v(Gson().toJson(obj))
            }
        }
    }

}