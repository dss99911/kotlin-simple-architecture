package kim.jeonghyeon.sample

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel
import kim.jeonghyeon.sample.mvvm.SampleMVVMFragment
import kim.jeonghyeon.sample.web.ChromeCustomTabFragment

class MainViewModel : BaseViewModel() {

    fun onClickChromeCustomTab() {
        addFragment(R.id.layout_container, ChromeCustomTabFragment())
    }

    fun onClickMVVM() {
        replaceFragment(R.id.layout_container, SampleMVVMFragment())
    }
}