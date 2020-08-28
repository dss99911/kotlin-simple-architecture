package kim.jeonghyeon.util

import com.google.gson.Gson
import kim.jeonghyeon.androidlibrary.extension.isDebug
import kim.jeonghyeon.androidlibrary.extension.isTesting
import timber.log.Timber

actual class Logger actual constructor() {
    actual inline fun i(message: String) {
        //todo print on product as well?
        if (isDebug) {
            if (isTesting) {
                println(message)
            } else {
                Timber.i(message)
            }
        }
    }
    actual inline fun i(vararg obj: Any?) {
        if (isDebug) {
            if (isTesting) {
                println(Gson().toJson(obj))
            } else {
                Timber.i(Gson().toJson(obj))
            }
        }

    }
    actual inline fun d(message: String) {
        if (isDebug) {
            if (isTesting) {
                println(message)
            } else {
                Timber.d(message)
            }
        }

    }
    actual inline fun d(vararg obj: Any?) {
        if (isDebug) {
            if (isTesting) {
                println(Gson().toJson(obj))
            } else {
                Timber.d(Gson().toJson(obj))
            }
        }
    }
    actual inline fun e(e: Throwable) {
        //todo print on product as well?
        if (isDebug) {
            if (isTesting) {
                e.printStackTrace()
            } else {
                Timber.e(e)
            }
        }
    }
    actual inline fun e(e: String) {
        if (isDebug) {
            if (isTesting) {
                println(e)
            } else {
                Timber.e(e)
            }
        }
    }
    actual inline fun e(vararg obj: Any?) {
        if (isDebug) {
            if (isTesting) {
                println(Gson().toJson(obj))
            } else {
                Timber.e(Gson().toJson(obj))
            }
        }
    }
}