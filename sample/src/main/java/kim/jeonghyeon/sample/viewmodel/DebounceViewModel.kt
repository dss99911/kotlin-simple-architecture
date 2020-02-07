package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.androidlibrary.architecture.livedata.liveObject
import kim.jeonghyeon.androidlibrary.architecture.livedata.liveResource
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel

class DebounceViewModel : BaseViewModel() {
    val count = liveObject<Int>(0)
    val inputText = liveObject<String>()
    val result = liveResource<String>().apply {
        addSource(inputText) {
            loadDebounce(1000) {
                it.toUpperCase()
            }
        }
    }

    val test = liveResource<Unit>()
    fun refresh() {
        test.loadDebounce(1000) {
            count.value = count.value!! + 1
            Unit
        }
    }
}