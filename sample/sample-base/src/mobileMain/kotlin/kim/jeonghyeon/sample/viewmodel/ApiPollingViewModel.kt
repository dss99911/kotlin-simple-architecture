package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.coroutine.polling
import kim.jeonghyeon.sample.api.Post
import kim.jeonghyeon.sample.di.serviceLocator

class ApiPollingViewModel(/*private val api: SampleApi*/) : BaseViewModel() {

    //todo required for ios to create instance, currently kotlin doesn't support predefined parameter
    // if it's supported, remove this
//    constructor(): this(serviceLocator.sampleApi)

    val result = dataFlow("")
    val count = dataFlow(0)

    override fun onInitialized() {
        result.load(status) {
            //todo after this fixed https://youtrack.jetbrains.com/issue/KTOR-973
            // use constructor parameter
            val token = serviceLocator.sampleApi.getToken("id", "pw")

            polling(5, 1000, 3000) {
                count.value = it
                //todo after this fixed https://youtrack.jetbrains.com/issue/KTOR-973
                // use constructor parameter
                serviceLocator.sampleApi.submitPost(token, Post(1, "name$it"))
                it.toString()//show count
            }
        }
    }
}