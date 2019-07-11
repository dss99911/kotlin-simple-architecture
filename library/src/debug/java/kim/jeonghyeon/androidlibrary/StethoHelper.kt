package kim.jeonghyeon.androidlibrary

import android.app.Application
import com.facebook.stetho.Stetho

object StethoHelper {
    fun initialize(app: Application) {
        Stetho.initializeWithDefaults(app)
    }
}