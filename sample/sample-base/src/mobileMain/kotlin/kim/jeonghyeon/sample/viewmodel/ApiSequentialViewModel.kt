package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.api.PreferenceApi
import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.sample.di.serviceLocator

class ApiSequentialViewModel(private val api: PreferenceApi) : BaseViewModel() {

    //todo required for ios to create instance, currently kotlin doesn't support predefined parameter
    // if it's supported, remove this
    constructor(): this(serviceLocator.preferenceApi)

    val KEY1 = "key1"
    val KEY2 = "key2"
    val KEY3 = "key3"

    val list = dataFlow(listOf<Pair<String, String?>>())
    val input1 = dataFlow("")
    val input2 = dataFlow("")
    val input3 = dataFlow("")

    val textList = dataFlow(listOf<String>()).withSource(list) {
        value = it.map { "key : ${it.first}, value : ${it.second}" }
    }

    override fun onInitialized() {
        list.load(initStatus) {
            listOf(
                Pair(KEY1, api.getString(KEY1)).also { input1.value = it.second ?: "" },
                Pair(KEY2, api.getString(KEY2)).also { input2.value = it.second ?: "" },
                Pair(KEY3, api.getString(KEY3)).also { input3.value = it.second ?: "" }
            )
        }
    }

    fun onClick() {
        list.load(status) {
            api.setString(KEY1, input1.value)
            api.setString(KEY2, input2.value)
            api.setString(KEY3, input3.value)
            listOf(
                Pair(KEY1, input1.value),
                Pair(KEY2, input2.value),
                Pair(KEY3, input3.value)
            )
        }
    }

}