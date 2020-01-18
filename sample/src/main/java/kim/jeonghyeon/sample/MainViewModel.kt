package kim.jeonghyeon.sample

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel
import kim.jeonghyeon.androidlibrary.extension.toast

class MainViewModel : BaseViewModel() {
    fun launchNavigateFragment() {
        MainFragmentDirections.actionMainFragmentToNavigationFragment()
            .apply { abc = 10 }
            .let { navigateDirection(it) }

    }

    fun onClickTest() {
        toast("test")
    }
}