package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.androidlibrary.architecture.livedata.LiveObject
import kim.jeonghyeon.androidlibrary.architecture.livedata.LiveResource
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel

class DebounceViewModel : BaseViewModel() {
    val inputText = LiveObject<String>()
    val result = LiveResource<String>().apply {
        addSource(inputText) {
            loadDebounce(1000) {
                it.toUpperCase()
            }
        }
    }
}