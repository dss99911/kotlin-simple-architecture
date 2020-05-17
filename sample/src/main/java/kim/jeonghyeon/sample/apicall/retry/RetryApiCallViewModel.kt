package kim.jeonghyeon.sample.apicall.retry

import kim.jeonghyeon.androidlibrary.architecture.livedata.LiveResource
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel
import kim.jeonghyeon.sample.apicall.coroutine.CoroutineApi

class RetryApiCallViewModel(val api: CoroutineApi) : BaseViewModel() {
    val result = LiveResource<Unit>()

    init {
        call()
    }

    fun call() {
        //status is set on initState LiveData. and it's observed by BaseFragment.
        //on error, shows Snackbar, and if user click retry button on snackbar. api is called again.
        result(initState) {
            //server throw error
            api.getError()
        }
    }
}
