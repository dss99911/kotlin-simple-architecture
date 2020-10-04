package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.client.DataFlow
import kim.jeonghyeon.net.headerKeyValue
import kim.jeonghyeon.sample.api.SampleApi
import kim.jeonghyeon.sample.di.serviceLocator

/**
 * shows how backend get header and how common header is working.
 */
class ApiHeaderViewModel(private val api: SampleApi) : SampleViewModel() {

    //todo required for ios to create instance, currently kotlin doesn't support predefined parameter
    // if it's supported, remove this
    constructor(): this(serviceLocator.sampleApi)

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