package kim.jeonghyeon.sample.apicall.parallel

import kim.jeonghyeon.androidlibrary.architecture.livedata.LiveResource
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel
import kim.jeonghyeon.sample.apicall.Item
import kim.jeonghyeon.sample.apicall.PostRequestBody
import kim.jeonghyeon.sample.apicall.coroutine.CoroutineApi
import kotlinx.coroutines.async

class ParallelApiCallViewModel(val api: CoroutineApi) : BaseViewModel() {
    val result = LiveResource<Unit>()

    init {
        postItems()
    }

    fun postItems() {
        result.load {
            val token = api.getToken()

            val result1 = async { api.submitPost(PostRequestBody(token, Item(1, "name1"))) }
            val result2 = async { api.submitPost(PostRequestBody(token, Item(2, "name2"))) }
            val result3 = async { api.submitPost(PostRequestBody(token, Item(3, "name3"))) }
            result1.await()
            result2.await()
            result3.await()
        }
    }
}
