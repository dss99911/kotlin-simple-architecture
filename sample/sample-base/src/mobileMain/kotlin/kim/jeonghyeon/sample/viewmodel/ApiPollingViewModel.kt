package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.client.DataFlow
import kim.jeonghyeon.coroutine.polling
import kim.jeonghyeon.sample.api.Post
import kim.jeonghyeon.sample.api.SampleApi
import kim.jeonghyeon.sample.di.serviceLocator

class ApiPollingViewModel(private val api: SampleApi) : SampleViewModel() {

    //todo required for ios to create instance, currently kotlin doesn't support predefined parameter
    // if it's supported, remove this
    constructor(): this(serviceLocator.sampleApi)

    val result by add { DataFlow<String>() }
    val count by add { DataFlow(0) }

    override fun onInit() {
        result.load(status) {
            val token = api.getToken("id", "pw")

            polling(5, 1000, 3000) {
                count.setValue(it)
                api.submitPost(token, Post(1, "name$it"))
                it.toString()//show count
            }
        }
    }
}