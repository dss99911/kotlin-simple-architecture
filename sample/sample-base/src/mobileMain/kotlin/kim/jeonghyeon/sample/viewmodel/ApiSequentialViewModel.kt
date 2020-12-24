package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.api.PreferenceApi
import kim.jeonghyeon.client.viewModelFlow
import kim.jeonghyeon.sample.di.serviceLocator
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

/**
 * call multiple apis sequentially
 */
class ApiSequentialViewModel(private val api: PreferenceApi = serviceLocator.preferenceApi) : ModelViewModel() {

    //todo [KSA-48] support localization on kotlin side
    override val title: String = "Api call sequentially"

    val KEY1 = "key1"
    val KEY2 = "key2"
    val KEY3 = "key3"

    val list = viewModelFlow(listOf<Pair<String, String?>>())
    val input1 = viewModelFlow("")
    val input2 = viewModelFlow("")
    val input3 = viewModelFlow("")

    val textList = list.map {
        it.map { "key : ${it.first}, value : ${it.second}" }
    }.toData()

    override fun onInit() {
        list.load(initStatus) {
            listOf(
                Pair(KEY1, api.getStringPerUser(KEY1)).also { input1.value = it.second ?: "" },
                Pair(KEY2, api.getStringPerUser(KEY2)).also { input2.value = it.second ?: "" },
                Pair(KEY3, api.getStringPerUser(KEY3)).also { input3.value = it.second ?: "" }
            )
        }
    }

    fun onClick() {
        list.loadInIdle(status) {
            api.setStringPerUser(KEY1, input1.value)
            api.setStringPerUser(KEY2, input2.value)
            api.setStringPerUser(KEY3, input3.value)
            listOf(
                Pair(KEY1, input1.value),
                Pair(KEY2, input2.value),
                Pair(KEY3, input3.value)
            )
        }
    }
}

// TODO reactive way.
//class ApiSequentialViewModel2(private val api: PreferenceApi = serviceLocator.preferenceApi) : ModelViewModel() {
//
//    //todo [KSA-48] support localization on kotlin side
//    override val title: String = "Api call sequentially"
//
//    val KEY1 = "key1"
//    val KEY2 = "key2"
//    val KEY3 = "key3"
//
//
//    val input1 by add { viewModelFlow("") }
//    val input2 by add { viewModelFlow("") }
//    val input3 by add { viewModelFlow("") }
//    val click = viewModelFlow<Unit>()
//
//
//    val list by add { viewModelFlow(listOf<Pair<String, String?>>()) }
//    val textList by add {
//        merge(
//            initFlow
//                .map {
//                    listOf(
//                        Pair(KEY1, api.getStringPerUser(KEY1)).also { input1.value = it.second ?: "" },
//                        Pair(KEY2, api.getStringPerUser(KEY2)).also { input2.value = it.second ?: "" },
//                        Pair(KEY3, api.getStringPerUser(KEY3)).also { input3.value = it.second ?: "" }
//                    )
//                }
//                .toData(initStatus),
//            click
//                .mapInIdle {
//                    api.setStringPerUser(KEY1, input1.value)
//                    api.setStringPerUser(KEY2, input2.value)
//                    api.setStringPerUser(KEY3, input3.value)
//                    listOf(
//                        Pair(KEY1, input1.value),
//                        Pair(KEY2, input2.value),
//                        Pair(KEY3, input3.value)
//                    )
//                }
//                .toData(status)
//        ).map {
//            it.map { "key : ${it.first}, value : ${it.second}" }
//        }
//    }
//}