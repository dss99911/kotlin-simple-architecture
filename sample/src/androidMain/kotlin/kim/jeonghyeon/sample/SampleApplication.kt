package kim.jeonghyeon.sample

import kim.jeonghyeon.androidlibrary.BaseApplication

class SampleApplication : BaseApplication() {

    override val isProd: Boolean
        get() = true
    override val isMock: Boolean
        get() = false
    override val isDebug: Boolean
        get() = false

    override fun onCreated() {
    }
}