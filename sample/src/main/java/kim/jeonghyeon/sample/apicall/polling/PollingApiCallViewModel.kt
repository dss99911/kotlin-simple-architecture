package kim.jeonghyeon.sample.apicall.polling

import kim.jeonghyeon.androidlibrary.architecture.livedata.LiveResource
import kim.jeonghyeon.androidlibrary.architecture.livedata.polling
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel
import kim.jeonghyeon.sample.apicall.Item
import kim.jeonghyeon.sample.apicall.PostRequestBody
import kim.jeonghyeon.sample.apicall.coroutine.CoroutineApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PollingApiCallViewModel(val api: CoroutineApi) : BaseViewModel() {
    val result = LiveResource<Unit>()
    var mockResult = false

    init {
        postItems()
    }

    fun postItems() {
        result {
            //change result to true
            launch {
                delay(3000)
                mockResult = true
            }

            val token = api.getToken()
            api.submitPost(PostRequestBody(token, Item(1, "name1")))

            //call api 5 times, initial delay 500ms, repeating delay 500ms
            polling(5, 500, 1000) {
                //status is true or false.
                val status = api.getStatus(mockResult)
                //check() is kotlin top-level function. if status is not true. throw exception.
                //and polling() try to call again until status is true.
                check(status)
            }
        }
    }
}
