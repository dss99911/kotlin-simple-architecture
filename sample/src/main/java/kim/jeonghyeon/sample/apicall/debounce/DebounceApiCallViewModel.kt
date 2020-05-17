package kim.jeonghyeon.sample.apicall.debounce

import kim.jeonghyeon.androidlibrary.architecture.livedata.LiveObject
import kim.jeonghyeon.androidlibrary.architecture.livedata.LiveResource
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel
import kim.jeonghyeon.sample.apicall.coroutine.CoroutineApi

class DebounceApiCallViewModel(val api: CoroutineApi) : BaseViewModel() {
    //inputText value is changed by user input
    val inputText = LiveObject<String>()
    val result = LiveResource<String>().apply {
        addSource(inputText) {
            //if inputText is changed. this is called
            loadDebounce(1000) {
                //this is called after 1 sec later. and cancel previous job if exists.
                api.getToken() + it.toUpperCase()
            }
        }
    }
}
