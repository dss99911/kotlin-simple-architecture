package kim.jeonghyeon.sample.web

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel

class ChromeCustomTabViewModel : BaseViewModel() {
    init {
        performWithActivity {
            ChromeCustomTabSample.showCustomTab(it, "http://google.com")
        }
    }
}