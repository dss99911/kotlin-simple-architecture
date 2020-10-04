package kim.jeonghyeon.sample.viewmodel

import io.ktor.http.*
import kim.jeonghyeon.client.DataFlow

/**
 * this shows how to receive data and return result
 */
class DeeplinkSubViewModel : SampleViewModel() {

    val result by add { DataFlow<String>() }

    /**
     * receive data
     */
    override fun onDeeplinkReceived(url: Url) {
        result.setValue(url.getParam(0, REQUEST_TYPE) ?: "")
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
