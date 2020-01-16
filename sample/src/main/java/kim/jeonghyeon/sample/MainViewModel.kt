package kim.jeonghyeon.sample

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel
import kim.jeonghyeon.androidlibrary.architecture.mvvm.LiveEvent

class MainViewModel : BaseViewModel() {
    fun launchNavigateFragment() {
        MainFragmentDirections.actionMainFragmentToNavigationFragment()
            .apply { abc = 10 }
            .let { navigateDirection(it) }

    }

    val clickEvent = LiveEvent<Unit>()
}