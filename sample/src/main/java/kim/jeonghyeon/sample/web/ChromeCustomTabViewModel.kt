package kim.jeonghyeon.sample.web

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel

class ChromeCustomTabViewModel : BaseViewModel() {
    override fun onCreate() {
        performWithActivity {
            ChromeCustomTabSample.showCustomTab(it, "http://google.com")
        }


    }
}