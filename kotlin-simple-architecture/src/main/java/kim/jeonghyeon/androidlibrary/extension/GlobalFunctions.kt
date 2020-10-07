@file:Suppress("unused")

package kim.jeonghyeon.androidlibrary.extension

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.IntDef
import androidx.core.content.ContextCompat
import kim.jeonghyeon.androidlibrary.BuildConfig
import kim.jeonghyeon.androidlibrary.SimpleInitProvider
import org.jetbrains.anko.telephonyManager


val isTesting = noThrow { Class.forName("androidx.test.espresso.Espresso") } != null
        || noThrow { Class.forName("org.robolectric.RobolectricTestRunner") } != null

inline val isDebug get() = BuildConfig.DEBUG

val ctx: Context inline get() = SimpleInitProvider.instance

val handler by lazy { Handler(Looper.getMainLooper()) }

val androidId by lazy {
    getAndroidId()
}

@SuppressLint("HardwareIds")
private fun getAndroidId() {
    Settings.Secure.getString(ctx.contentResolver, Settings.Secure.ANDROID_ID)
}

@Suppress("DEPRECATION")
val imei: String?
    @SuppressLint("HardwareIds", "MissingPermission")
    get() =
        when {
            ContextCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED -> null
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> ctx.telephonyManager.imei
            else -> ctx.telephonyManager.deviceId
        }


fun toast(text: String?, duration: Int) {
    handler.post { Toast.makeText(ctx, text, duration).show() }
}

fun toast(text: String?) {
    toast(text, Toast.LENGTH_SHORT)
}

fun toast(textResId: Int) {
    toast(ctx.getString(textResId))
}

fun toastLong(text: String?) {
    toast(text, Toast.LENGTH_LONG)
}

fun toastLong(textResId: Int) {
    toast(ctx.getString(textResId), Toast.LENGTH_LONG)
}


inline fun <T> noThrow(action: () -> T): T? {
    return try {
        action()
    } catch (e: Exception) {
        null
    }
}

@IntDef(value = [Build.VERSION_CODES.N, Build.VERSION_CODES.M, Build.VERSION_CODES.LOLLIPOP], flag = true)
@Retention(AnnotationRetention.SOURCE)
annotation class VersionParam

fun isFromVersion(@VersionParam version: Int): Boolean = Build.VERSION.SDK_INT >= version

