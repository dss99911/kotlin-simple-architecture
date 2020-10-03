package kim.jeonghyeon.sample.viewmodel

import io.ktor.http.*
import kim.jeonghyeon.client.DeeplinkNavigation
import kim.jeonghyeon.client.DeeplinkResultListener
import kim.jeonghyeon.client.ScreenResult
import kim.jeonghyeon.const.DeeplinkUrl
import kim.jeonghyeon.net.RedirectionType
import kim.jeonghyeon.sample.api.SampleApi
import kim.jeonghyeon.sample.di.serviceLocator

/**
 * this shows how to receive data and return result
 */
class DeeplinkSubViewModel : SampleViewModel() {

    val result = dataFlow("")

    /**
     * receive data
     */
    override fun onDeeplinkReceived(url: Url) {
        result.value = url.getParam(0, REQUEST_TYPE)?: ""
    }

    fun onClickOk() {
        goBackWithOk(result.value)
    }

    companion object {
        // if nullable is required, use code like the below
        // url.getParam<String?>
        // result.dataOf<String?>()
        val REQUEST_TYPE = String::class
        val RESPONSE_TYPE = String::class
    }
}
