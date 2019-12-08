package kim.jeonghyeon.sample.mvvm

import kim.jeonghyeon.androidlibrary.architecture.livedata.Resource
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel
import kim.jeonghyeon.sample.MainActivityViewModel

class SampleParentViewModel(parent: MainActivityViewModel) :
    BaseViewModel() {

    init {
        parent.state.value = Resource.Loading
    }
}