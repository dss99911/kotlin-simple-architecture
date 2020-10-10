package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.api.PreferenceApi
import kim.jeonghyeon.client.DataFlow
import kim.jeonghyeon.sample.di.serviceLocator

/**
 * call multiple apis sequentially
 */
class ApiSequentialViewModel(private val api: PreferenceApi) : SampleViewModel() {

    //todo required for ios to create instance, currently kotlin doesn't support predefined parameter
    // if it's supported, remove this
    constructor(): this(serviceLocator.preferenceApi)

    val KEY1 = "key1"
    val KEY2 = "key2"
    val KEY3 = "key3"

    val list by add { DataFlow(listOf<Pair<String, String?>>()) }
    val input1 by add { DataFlow("") }
    val input2 by add { DataFlow("") }
    val input3 by add { DataFlow("") }

    val textList by add {
        DataFlow(listOf<String>()).withSource(list) {
            setValue(it.map { "key : ${it.first}, value : ${it.second}" })
        }
    }

    override fun onInit() {
        list.load(initStatus) {
            listOf(
                Pair(KEY1, api.getStringPerUser(KEY1)).also { input1.setValue(it.second ?: "") },
                Pair(KEY2, api.getStringPerUser(KEY2)).also { input2.setValue(it.second ?: "") },
                Pair(KEY3, api.getStringPerUser(KEY3)).also { input3.setValue(it.second ?: "") }
            )
        }
    }

    fun onClick() {
        list.load(status) {
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