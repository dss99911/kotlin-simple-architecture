package kim.jeonghyeon.sample.view.savedState

import androidx.lifecycle.SavedStateHandle
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel
import kim.jeonghyeon.androidlibrary.architecture.mvvm.savedStateDelegate
import kim.jeonghyeon.androidlibrary.architecture.mvvm.savedStateLiveData

class SavedStateViewModel(savedStateHandle: SavedStateHandle) : BaseViewModel(savedStateHandle) {
    var data by savedStateDelegate("data1", "empty data")
    val liveData by savedStateLiveData<String>("data1")

    val data2 by savedStateLiveData<String>()

    fun onResetClick() {
        data = "empty1"
        data2.value = "empty2"
    }
}
