package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.api.PreferenceApi
import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.client.viewModelFlow
import kim.jeonghyeon.client.viewModelFlow
import kim.jeonghyeon.sample.di.serviceLocator
import kim.jeonghyeon.util.log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

data class ApiSingleViewModel(private val api: PreferenceApi = serviceLocator.preferenceApi) : ModelViewModel() {

    //todo [KSA-48] support localization on kotlin side
    override val title: String = "Single Api call"

    override val signInRequired: Boolean = true

    private val KEY = "someKey"

    val result = viewModelFlow<String>()

    //if result is changed, input also changed.
    val input = result.toData()

    override fun onInit() {

        result.load(initStatus) {
            api.getStringPerUser(KEY) ?: ""
        }
    }

    fun onClick() {
        result.loadInIdle(status) {
            val text = input.valueOrNull?: error("please input")
            api.setStringPerUser(KEY, text)
            text//set changed text after api call is success
        }
    }
}

// TODO reactive way.
//class ApiSingleViewModel2(private val api: PreferenceApi = serviceLocator.preferenceApi) : ModelViewModel() {
//
//    //todo [KSA-48] support localization on kotlin side
//    override val title: String = "Single Api call"
//
//    override val signInRequired: Boolean = true
//
//    private val KEY = "someKey"
//
//    //if result is changed, input also changed.
//    val input: MutableSharedFlow<String> by add { result.toData() }
//    val click = viewModelFlow<Unit>()
//
//    val result by add {
//        merge(
//            initFlow
//                .map {
//                    api.getStringPerUser(KEY) ?: ""
//                }
//                .toData(initStatus),
//            click
//                .mapInIdle {
//                    val text = input.valueOrNull?: error("please input")
//                    api.setStringPerUser(KEY, text)
//                    text//set changed text after api call is success
//                }
//                .toData(status)
//        )
//    }
//}
