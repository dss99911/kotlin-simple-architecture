package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.client.DataFlow
import kim.jeonghyeon.net.headerKeyValue
import kim.jeonghyeon.sample.api.SampleApi
import kim.jeonghyeon.sample.di.serviceLocator

/**
 * shows how backend get header and how common header is working.
 */
class ApiHeaderViewModel(private val api: SampleApi = serviceLocator.sampleApi) : ModelViewModel() {

    //todo [KSA-48] support localization on kotlin side
    override val title: String = "Header Api call"

    val result by add { DataFlow<String>() }
    val input by add {
        DataFlow<String>().withSource(result)
    }


    override fun onInit() {
        result.load(initStatus) {
            api.getHeader()
        }
    }

    fun onClick() {
        result.load(status) {
            //change common header to check server receive changed header
            headerKeyValue = input.value?:error("please input header")
            api.getHeader()
        }
    }
}