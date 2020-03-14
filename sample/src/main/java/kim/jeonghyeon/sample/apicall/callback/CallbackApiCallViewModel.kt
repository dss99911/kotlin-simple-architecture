package kim.jeonghyeon.sample.apicall.callback

import kim.jeonghyeon.androidlibrary.architecture.livedata.LiveResource
import kim.jeonghyeon.androidlibrary.architecture.livedata.ResourceException
import kim.jeonghyeon.androidlibrary.architecture.livedata.postSuccess
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel
import kim.jeonghyeon.androidlibrary.architecture.net.error.ResourceError
import kim.jeonghyeon.androidlibrary.architecture.net.error.UnknownResourceError
import kim.jeonghyeon.sample.apicall.Item
import kim.jeonghyeon.sample.apicall.PostRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CallbackApiCallViewModel(val api: CallbackApi) : BaseViewModel() {
    val result = LiveResource<Unit>()

    init {
        postItem(Item(1, "name"))
    }

    fun postItem(item: Item) {
        getToken { token ->
            submitPost(token, item) {
                result.postSuccess(Unit)
            }
        }
    }

    fun getToken(onSuccess: (token: String) -> Unit) {
        api.getToken().load({ onSuccess(it) }) {
            result.value = it.asResource()
        }
    }

    fun submitPost(token: String, item: Item, onSuccess: (Unit) -> Unit) {
        api.submitPost(PostRequestBody(token, item)).load({ onSuccess(it) }) {
            result.value = it.asResource()
        }
    }
}

fun <T> Call<T>.load(onSuccess: (T) -> Unit, onError: (ResourceError) -> Unit) {
    enqueue(ResourceCallback<T>(onSuccess, onError))
}

class ResourceCallback<T>(val onSuccess: (T) -> Unit, val onError: (ResourceError) -> Unit) :
    Callback<T> {
    override fun onResponse(call: Call<T>, response: Response<T>) {
        onSuccess(response.body() as T)
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        if (t is ResourceException) {
            onError(t.error)
        } else {
            onError(UnknownResourceError(t))
        }
    }
}