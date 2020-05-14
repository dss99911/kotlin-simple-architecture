package kim.jeonghyeon.sample.etc.web

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel
import kim.jeonghyeon.androidlibrary.extension.ctx

class ChromeCustomTabViewModel : BaseViewModel() {
    init {
        //todo move to fragment
        ChromeCustomTabSample.showCustomTab(ctx, "http://google.com")
    }
}