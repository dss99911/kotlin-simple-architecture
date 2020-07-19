package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.api.PreferenceApi
import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.sample.di.serviceLocator
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow

class ApiParallelViewModel(private val api: PreferenceApi = serviceLocator.preferenceApi) : BaseViewModel() {
    val KEY1 = "key1"
    val KEY2 = "key2"
    val KEY3 = "key3"


    val list = MutableStateFlow(listOf<Pair<String, String?>>())
    val input1 = MutableStateFlow("")
    val input2 = MutableStateFlow("")
    val input3 = MutableStateFlow("")

    override fun onInitialized() {
        list.load(initStatus) {
            val a1 = async { api.getString(KEY1) }
            val a2 = async { api.getString(KEY2) }
            val a3 = async { api.getString(KEY3) }
            listOf(
                Pair(KEY1, a1.await()).also { input1.value = it.second ?: "" },
                Pair(KEY2, a2.await()).also { input2.value = it.second ?: "" },
                Pair(KEY3, a3.await()).also { input3.value = it.second ?: "" }
            )
        }
    }

    fun onClick() {
        list.load(status) {
            val a1 = async { api.setString(KEY1, input1.value) }
            val a2 = async { api.setString(KEY2, input2.value) }
            val a3 = async { api.setString(KEY3, input3.value) }
            a1.await()
            a2.await()
            a3.await()

            listOf(
                Pair(KEY1, input1.value),
                Pair(KEY2, input2.value),
                Pair(KEY3, input3.value)
            )
        }
    }
}