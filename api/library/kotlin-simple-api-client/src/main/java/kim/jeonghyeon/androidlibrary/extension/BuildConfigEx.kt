package kim.jeonghyeon.androidlibrary.extension

import android.content.Context
import kim.jeonghyeon.androidlibrary.SimpleInitProvider
import kim.jeonghyeon.api.BuildConfig

inline val isDebug get() = BuildConfig.DEBUG
val ctx: Context inline get() = SimpleInitProvider.instance

val isTesting = noThrow { Class.forName("androidx.test.espresso.Espresso") } != null
        || noThrow { Class.forName("org.robolectric.RobolectricTestRunner") } != null


inline fun <T> noThrow(action: () -> T): T? {
    return try {
        action()
    } catch (e: Exception) {
        null
    }
}