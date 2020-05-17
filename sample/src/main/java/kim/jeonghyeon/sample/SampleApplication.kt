package kim.jeonghyeon.sample

import kim.jeonghyeon.androidlibrary.BaseApplication
import kim.jeonghyeon.androidlibrary.extension.isTesting

class SampleApplication : BaseApplication() {
    override fun getKoinModules() = listOf(appModule)

    override val isProd: Boolean
        get() = BuildConfig.isProd
    override val isMock: Boolean
        get() = BuildConfig.isMock
    override val isDebug: Boolean
        get() = BuildConfig.DEBUG

    override fun onCreated() {
        if (!isProd || isDebug) {
            if (!isTesting) {
                //has exception on testing
                StethoHelper.initialize(this)
            }
        }
    }
}