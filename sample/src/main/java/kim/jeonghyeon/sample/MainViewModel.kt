package kim.jeonghyeon.sample

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel

class MainViewModel : BaseViewModel() {
    fun launchNavigateFragment() {
        MainFragmentDirections.actionMainFragmentToNavigationFragment()
            .apply { abc = 10 }
            .let { launchDirection(it) }

    }
}