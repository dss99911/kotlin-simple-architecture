package kim.jeonghyeon.sample.viewmodel.startactivity

import android.app.Activity
import kim.jeonghyeon.androidlibrary.architecture.livedata.LiveResource
import kim.jeonghyeon.androidlibrary.architecture.livedata.ResourceException
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel
import kim.jeonghyeon.androidlibrary.architecture.net.error.MessageError
import kim.jeonghyeon.androidlibrary.extension.ctx
import kim.jeonghyeon.sample.apicall.coroutine.CoroutineApi

class StartActivityViewModel(val api: CoroutineApi) : BaseViewModel() {
    val result = LiveResource<String>()

    fun onClick() {
        result(state) {
            val token = api.getToken()
            val (resultCode) = startActivityForResult(
                StartActivityActivity.getStartIntent(
                    ctx,
                    token
                )
            )
            if (resultCode != Activity.RESULT_OK) {
                throw ResourceException(MessageError("it's cancelled"))
            }

            "It's success"
        }

    }
}
