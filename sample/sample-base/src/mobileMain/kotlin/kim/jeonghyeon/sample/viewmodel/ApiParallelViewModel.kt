package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.api.PreferenceApi
import kim.jeonghyeon.client.viewModelFlow
import kim.jeonghyeon.sample.di.serviceLocator
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

/**
 * shows how to call multiple apis in parallel
 */
class ApiParallelViewModel(private val api: PreferenceApi = serviceLocator.preferenceApi) : ModelViewModel() {

    //todo [KSA-48] support localization on kotlin side
    override val title: String = "Api call in parallel"

    val KEY1 = "key1"
    val KEY2 = "key2"
    val KEY3 = "key3"

    val list = viewModelFlow(listOf<Pair<String, String?>>())
    val input1 = viewModelFlow<String>()
    val input2 = viewModelFlow<String>()
    val input3 = viewModelFlow<String>()

    override fun onInit() {
        list.load(initStatus) {
            val a1 = async { api.getStringPerUser(KEY1) }
            val a2 = async { api.getStringPerUser(KEY2) }
            val a3 = async { api.getStringPerUser(KEY3) }
            listOf(
                Pair(KEY1, a1.await()).also { input1.value = it.second?: ""  },
                Pair(KEY2, a2.await()).also { input2.value = it.second?: ""  },
                Pair(KEY3, a3.await()).also { input3.value = it.second?: ""  }
            )
        }
    }

    fun onClick() {
        list.loadInIdle(status) {
            val a1 = async { api.setStringPerUser(KEY1, input1.valueOrNull) }
            val a2 = async { api.setStringPerUser(KEY2, input2.valueOrNull) }
            val a3 = async { api.setStringPerUser(KEY3, input3.valueOrNull) }
            a1.await()
            a2.await()
            a3.await()

            listOf(
                Pair(KEY1, input1.valueOrNull),
                Pair(KEY2, input2.valueOrNull),
                Pair(KEY3, input3.valueOrNull)
            )
        }
    }
}

// TODO reactive way.
//class ApiParallelViewModel2(private val api: PreferenceApi = serviceLocator.preferenceApi) : ModelViewModel() {
//
//    //todo [KSA-48] support localization on kotlin side
//    override val title: String = "Api call in parallel"
//
//    val KEY1 = "key1"
//    val KEY2 = "key2"
//    val KEY3 = "key3"
//
//    val input1 by add { viewModelFlow<String>() }
//    val input2 by add { viewModelFlow<String>() }
//    val input3 by add { viewModelFlow<String>() }
//    val click = viewModelFlow<Unit>()
//
//    val list by add {
//        merge(
//            initFlow
//                .map {
//                    val a1 = scope.async { api.getStringPerUser(KEY1) }
//                    val a2 = scope.async { api.getStringPerUser(KEY2) }
//                    val a3 = scope.async { api.getStringPerUser(KEY3) }
//                    listOf(
//                        Pair(KEY1, a1.await()).also { input1.value = it.second?: ""  },
//                        Pair(KEY2, a2.await()).also { input2.value = it.second?: ""  },
//                        Pair(KEY3, a3.await()).also { input3.value = it.second?: ""  }
//                    )
//                }
//                .toData(initStatus),
//            click
//                .mapInIdle {
//                    val a1 = scope.async { api.setStringPerUser(KEY1, input1.valueOrNull) }
//                    val a2 = scope.async { api.setStringPerUser(KEY2, input2.valueOrNull) }
//                    val a3 = scope.async { api.setStringPerUser(KEY3, input3.valueOrNull) }
//                    a1.await()
//                    a2.await()
//                    a3.await()
//
//                    listOf(
//                        Pair(KEY1, input1.valueOrNull),
//                        Pair(KEY2, input2.valueOrNull),
//                        Pair(KEY3, input3.valueOrNull)
//                    )
//                }
//        )
//    }
//}