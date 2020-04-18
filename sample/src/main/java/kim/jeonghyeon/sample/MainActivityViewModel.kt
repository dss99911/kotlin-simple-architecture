package kim.jeonghyeon.sample

import kim.jeonghyeon.androidlibrary.architecture.livedata.LiveObject
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel

class MainActivityViewModel : BaseViewModel() {
    val text = "parent"
    val title = LiveObject<String>(" ")//if it is empty, shows default title of app name

    fun onCollapsingStatusChanged(status: CollapsingStatus) {
        title.value = status.name
    }
}