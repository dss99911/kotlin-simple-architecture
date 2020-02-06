@file:Suppress("unused")

package kim.jeonghyeon.androidlibrary.extension

import com.google.gson.Gson
import timber.log.Timber

//fun log(text: String, tag: String = getClassName(), withStackTrace: Boolean = false) {
//    if (isDebug) {
//        Log.d(tag, text + if (withStackTrace) getCodeLocation(); else "")
//    }
//}

//fun log(tag: String, text: String, e: Throwable) {
//    if (isDebug) {
//        Log.d(tag, text, e)
//    }
//}

@Suppress("NOTHING_TO_INLINE")
inline fun log(e: Throwable) {
    if (!isProdRelease) {
        if (isTesting) {
            e.printStackTrace()
        } else {
            Timber.e(e)
        }
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun log(vararg obj: Any?) {
    if (!isProdRelease) {
        if (isTesting) {
            println(Gson().toJson(obj))
        } else {
            Timber.v(Gson().toJson(obj))
        }

    }
}
