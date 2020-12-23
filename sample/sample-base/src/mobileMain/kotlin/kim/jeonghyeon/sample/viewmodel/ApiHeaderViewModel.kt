package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.client.flowViewModel
import kim.jeonghyeon.client.value
import kim.jeonghyeon.client.valueOrNull
import kim.jeonghyeon.net.headerKeyValue
import kim.jeonghyeon.sample.api.SampleApi
import kim.jeonghyeon.sample.di.serviceLocator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

/**
 * shows how backend get header and how common header is working.
 */
class ApiHeaderViewModel(private val api: SampleApi = serviceLocator.sampleApi) : ModelViewModel() {

    //todo [KSA-48] support localization on kotlin side
    override val title: String = "Header Api call"

    val result by add { flowViewModel<String>() }
    val input by add {
        result.toData()
    }


    override fun onInit() {
        result.load(initStatus) {
            api.getHeader()
        }
    }

    fun onClick() {
        result.loadInIdle(status) {
            //change common header to check server receive changed header
            headerKeyValue = input.value?:error("please input header")
            api.getHeader()
        }
    }
}

class ApiHeaderViewModel2(private val api: SampleApi = serviceLocator.sampleApi) : ModelViewModel() {

    //todo [KSA-48] support localization on kotlin side
    override val title: String = "Header Api call"

    val click = flowViewModel<Unit>()
    val input by add { result.toData() }

    val result: Flow<String> by add {
        merge(
            initFlow
                .map { api.getHeader() }
                .toData(initStatus),
            click.mapInIdle {
                //change common header to check server receive changed header
                headerKeyValue = input.valueOrNull?:error("please input header")
                api.getHeader()
            }.toData(status)
        )
    }

}