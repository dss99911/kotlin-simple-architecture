package kim.jeonghyeon.sample.ext

import kim.jeonghyeon.androidlibrary.util.Logger
import kim.jeonghyeon.sample.BuildConfig

@Suppress("NOTHING_TO_INLINE")
inline fun log(e: Throwable) {
    if (!BuildConfig.isProd || BuildConfig.DEBUG) {
        Logger.log(e)
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun log(message: String) {
    if (!BuildConfig.isProd || BuildConfig.DEBUG) {
        Logger.log(message)
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun log(vararg obj: Any?) {
    if (!BuildConfig.isProd || BuildConfig.DEBUG) {
        Logger.log(obj)
    }
}