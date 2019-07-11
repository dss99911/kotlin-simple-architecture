package kim.jeonghyeon.androidlibrary.util

import android.annotation.SuppressLint
import android.net.ConnectivityManager
import kim.jeonghyeon.androidlibrary.extension.ctx
import org.jetbrains.anko.connectivityManager


object NetworkUtil {
    @SuppressLint("MissingPermission")
    fun isConnected(): Boolean {
        return ctx.connectivityManager.activeNetworkInfo?.isConnected ?: false
    }

    @SuppressLint("MissingPermission")
    fun isMeteredConnected(): Boolean = ctx.connectivityManager.isActiveNetworkMetered

    @SuppressLint("MissingPermission")
    fun isUnmeteredConnected(): Boolean = isConnected() && !isMeteredConnected()
}