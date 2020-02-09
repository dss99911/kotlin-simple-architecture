package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.androidlibrary.architecture.livedata.liveObject
import kim.jeonghyeon.androidlibrary.architecture.livedata.liveResource
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel

class DebounceViewModel : BaseViewModel() {
    val inputText = liveObject<String>()
    val result = liveResource<String>().apply {
        addSource(inputText) {
            loadDebounce(1000) {
                it.toUpperCase()
            }
        }
    }
}