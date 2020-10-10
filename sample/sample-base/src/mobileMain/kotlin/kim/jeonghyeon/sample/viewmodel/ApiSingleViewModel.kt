package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.api.PreferenceApi
import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.client.DataFlow
import kim.jeonghyeon.sample.di.serviceLocator

class ApiSingleViewModel(private val api: PreferenceApi) : SampleViewModel() {
    override val signInRequired: Boolean = true

    //todo required for ios to create instance, currently kotlin doesn't support predefined parameter
    // if it's supported, remove this
    constructor() : this(serviceLocator.preferenceApi)

    private val KEY = "someKey"

    val result by add { DataFlow<String>() }

    //if result is changed, input also changed.
    val input by add { DataFlow<String>().withSource(result) }


    override fun onInit() {
        result.load(initStatus) {
            api.getStringPerUser(KEY) ?: ""
        }
    }

    fun onClick() {
        result.load(status) {
            val text = input.value?: error("please input")
            api.setStringPerUser(KEY, text)
            text//set changed text after api call is success
        }
    }
}
