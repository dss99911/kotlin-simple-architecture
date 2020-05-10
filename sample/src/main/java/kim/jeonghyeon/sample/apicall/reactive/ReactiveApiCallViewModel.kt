package kim.jeonghyeon.sample.apicall.reactive

import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kim.jeonghyeon.androidlibrary.architecture.livedata.LiveResource
import kim.jeonghyeon.androidlibrary.architecture.livedata.postError
import kim.jeonghyeon.androidlibrary.architecture.livedata.postSuccess
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel
import kim.jeonghyeon.androidlibrary.architecture.net.error.UnknownResourceError
import kim.jeonghyeon.sample.apicall.Item
import kim.jeonghyeon.sample.apicall.PostRequestBody

class ReactiveApiCallViewModel(val api: ReactiveApi) : BaseViewModel() {
    val result = LiveResource<Unit>()

    init {
        postItem(Item(1, "name"))
    }

    fun postItem(item: Item) {

        api.getToken()
            .flatMap { api.submitPost(PostRequestBody(it, item)) }
            .subscribeResource(result)
    }
}

fun <T> Flowable<T>.subscribeResource(resource: LiveResource<T>) {
    subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ resource.postSuccess(it) }, {
            resource.postError(UnknownResourceError(it))
        })
}