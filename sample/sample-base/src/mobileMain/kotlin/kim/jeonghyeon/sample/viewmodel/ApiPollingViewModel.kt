package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.coroutine.polling
import kim.jeonghyeon.sample.api.Post
import kim.jeonghyeon.sample.api.SimpleApi
import kim.jeonghyeon.sample.di.serviceLocator
import kotlinx.coroutines.flow.MutableStateFlow

class ApiPollingViewModel(private val api: SimpleApi = serviceLocator.simpleApi) : BaseViewModel() {
    val result = MutableStateFlow("")
    val count = MutableStateFlow(0)

    override fun onInitialized() {
        result.load(status) {
            val token = api.getToken("id", "pw")

            polling(5, 1000, 3000) {
                count.value = it
                api.submitPost(token, Post(1, "name$it"))
                it.toString()//show count
            }
        }
    }
}