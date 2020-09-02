package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.net.headerKeyValue
import kim.jeonghyeon.sample.api.SampleApi
import kim.jeonghyeon.sample.di.serviceLocator

/**
 * shows how backend get header and how common header is working.
 */
class ApiHeaderViewModel(private val api: SampleApi) : BaseViewModel() {

    //todo required for ios to create instance, currently kotlin doesn't support predefined parameter
    // if it's supported, remove this
    constructor(): this(serviceLocator.sampleApi)

    val result = dataFlow("")
    val input = dataFlow("")
        .withSource(result) { value = it }

    override fun onInitialized() {
        result.load(initStatus) {
            api.getHeader()
        }
    }

    fun onClick() {
        result.load(status) {
            //change common header to check server receivec changed header
            headerKeyValue = input.value
            api.getHeader()
        }
    }
}