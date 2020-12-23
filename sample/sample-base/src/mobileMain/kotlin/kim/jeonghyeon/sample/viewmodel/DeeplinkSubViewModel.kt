package kim.jeonghyeon.sample.viewmodel

import io.ktor.http.*
import kim.jeonghyeon.client.flowViewModel
import kim.jeonghyeon.client.value
import kim.jeonghyeon.type.Status
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * this shows how to receive data and return result
 */
class DeeplinkSubViewModel(val request: String) : ModelViewModel() {

    //todo [KSA-48] support localization on kotlin side
    override val title: String = "Deeplink Sub"

    val result by add { flowViewModel(request) }

    fun onClickOk() {
        goBackWithOk(result.value)
    }
}


class DeeplinkSubViewModel2(val request: String) : ModelViewModel() {

    //todo [KSA-48] support localization on kotlin side
    override val title: String = "Deeplink Sub"

    val result by add { flowViewModel(request) }

    val click = flowViewModel<Unit>().apply {
        collectOnViewModel {
            goBackWithOk(result.value)
        }
    }
}
