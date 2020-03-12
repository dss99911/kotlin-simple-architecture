package kim.jeonghyeon.sample

import kim.jeonghyeon.androidlibrary.BaseApplication

class SampleApplication : BaseApplication() {
    override fun getKoinModules() = listOf(appModule)
}