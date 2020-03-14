package kim.jeonghyeon.sample.apicall.threading

import kim.jeonghyeon.androidlibrary.architecture.livedata.LiveResource
import kim.jeonghyeon.androidlibrary.architecture.livedata.ResourceException
import kim.jeonghyeon.androidlibrary.architecture.livedata.postError
import kim.jeonghyeon.androidlibrary.architecture.livedata.postSuccess
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel
import kim.jeonghyeon.androidlibrary.extension.log
import kim.jeonghyeon.sample.apicall.Item
import kim.jeonghyeon.sample.apicall.PostRequestBody

class ThreadingApiCallViewModel(val api: ThreadingApi) : BaseViewModel() {
    val result = LiveResource<Unit>()

    init {
        postItem(Item(1, "name"))
    }

    fun postItem(item: Item) {
        result.loadData {
            api.submitPost(PostRequestBody(api.getToken(), item))
        }
    }
}

fun <T> LiveResource<T>.loadData(action: () -> T) {
    Thread {
        try {
            postSuccess(action())
        } catch (e: ResourceException) {
            log(e)
            postError(e.error)
        }
    }.start()
}

