package kim.jeonghyeon.sample.apicall.simple

import kim.jeonghyeon.androidlibrary.architecture.livedata.LiveResource
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel

class SimpleApiCallViewModel(val api: SimpleApi) : BaseViewModel() {

    val result = LiveResource<Any?>()

    init {
        postItem(Item(1, "name"))
    }

    fun postItem(item: Item) {
        result(initState) {
            api.submitPost(api.getToken().name, item)
        }
    }
}
