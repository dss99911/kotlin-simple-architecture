package kim.jeonghyeon.sample.mvvm

import androidx.lifecycle.SavedStateHandle
import kim.jeonghyeon.androidlibrary.architecture.livedata.Resource
import kim.jeonghyeon.androidlibrary.architecture.mvvm.ParentViewModel
import kim.jeonghyeon.sample.MainActivityViewModel

class SampleParentViewModel(savedStateHandle: SavedStateHandle) :
    ParentViewModel<MainActivityViewModel>(savedStateHandle) {

    init {
        parent.status.value = Resource.loading()
    }
}