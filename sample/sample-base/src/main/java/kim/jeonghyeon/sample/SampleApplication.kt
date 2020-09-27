package kim.jeonghyeon.sample

import android.app.Application
import kim.jeonghyeon.androidlibrary.extension.isTesting

class SampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            if (!isTesting) {
                //has exception on testing
                StethoHelper.initialize(this)
            }
        }
    }
}