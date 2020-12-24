package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.client.viewModelFlow
import kim.jeonghyeon.coroutine.polling
import kim.jeonghyeon.sample.api.SampleApi
import kim.jeonghyeon.sample.di.serviceLocator
import kotlinx.coroutines.flow.map

/**
 * after transaction finished, we check the transaction result is success or fail
 * so, call same api repeatedly.
 */
class ApiPollingViewModel(private val api: SampleApi = serviceLocator.sampleApi) : ModelViewModel() {

    //todo [KSA-48] support localization on kotlin side
    override val title: String = "polling"

    val result = viewModelFlow<String>()
    val count = viewModelFlow(0)

    override fun onInit() {
        result.load(status) {
            polling(5, 1000, 3000) {
                count.value = it
                api.getRandomError(2)
                it.toString()//show count
            }
        }
    }
}

// TODO reactive way.
//class ApiPollingViewModel2(private val api: SampleApi = serviceLocator.sampleApi) : ModelViewModel() {
//
//    //todo [KSA-48] support localization on kotlin side
//    override val title: String = "polling"
//
//    val result by add {
//        initFlow
//            .map {
//                polling(5, 1000, 3000) {
//                    count.value = it
//                    api.getRandomError(3)
//                    it.toString()//show count
//                }
//            }.toData(status)
//    }
//    val count by add { viewModelFlow(0) }
//}