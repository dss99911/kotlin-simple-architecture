package kim.jeonghyeon.sample

import kim.jeonghyeon.androidlibrary.BaseApplication
import kim.jeonghyeon.androidlibrary.extension.isTesting

class SampleApplication : BaseApplication() {
    override fun onCreated() {
        if (BuildConfig.DEBUG) {
            if (!isTesting) {
                //has exception on testing
                StethoHelper.initialize(this)
            }
        }
    }
}