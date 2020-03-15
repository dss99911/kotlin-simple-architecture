package kim.jeonghyeon.sample.apicall.chaining

import kim.jeonghyeon.androidlibrary.architecture.livedata.LiveResource
import kim.jeonghyeon.androidlibrary.architecture.livedata.plusAssign
import kim.jeonghyeon.androidlibrary.architecture.livedata.successSwitchMap
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel
import kim.jeonghyeon.sample.apicall.Item
import kim.jeonghyeon.sample.apicall.PostRequestBody

class ChainingApiCallViewModel(val api: ChainingApi) : BaseViewModel() {
    val result = LiveResource<Unit>()

    init {
        postItem(Item(1, "name"))
    }

    fun postItem(item: Item) {
        result += api.getToken()
            .successSwitchMap { token -> api.submitPost(PostRequestBody(token, item)) }
    }
}

//fun <X, Y> LiveResource<X>.successSwitchMap(@NonNull func: (X) -> LiveResource<Y>): LiveResource<Y> =
//    switchMap {
//        if (it is Resource.Success) {
//            func(it.data)
//        } else LiveResource(it as Resource<Y>)
//    }