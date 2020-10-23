package kim.jeonghyeon.sample.viewmodel

import io.ktor.http.*
import kim.jeonghyeon.client.DataFlow

/**
 * this shows how to receive data and return result
 */
class DeeplinkSubViewModel(val request: String) : ModelViewModel() {

    //todo [KSA-48] support localization on kotlin side
    override val title: String = "Deeplink Sub"

    val result by add { DataFlow(request) }

    fun onClickOk() {
        goBackWithOk(result.value)
    }
}
