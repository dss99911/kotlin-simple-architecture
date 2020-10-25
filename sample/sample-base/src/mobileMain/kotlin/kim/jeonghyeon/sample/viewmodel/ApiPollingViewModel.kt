package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.client.DataFlow
import kim.jeonghyeon.coroutine.polling
import kim.jeonghyeon.sample.api.SampleApi
import kim.jeonghyeon.sample.di.serviceLocator

/**
 * after transaction finished, we check the transaction result is success or fail
 * so, call same api repeatedly.
 */
class ApiPollingViewModel(private val api: SampleApi = serviceLocator.sampleApi) : ModelViewModel() {

    //todo [KSA-48] support localization on kotlin side
    override val title: String = "polling"

    val result by add { DataFlow<String>() }
    val count by add { DataFlow(0) }

    override fun onInit() {
        result.load(status) {
            polling(5, 1000, 3000) {
                count.setValue(it)
                api.getRandomError(3)
                it.toString()//show count
            }
        }
    }
}