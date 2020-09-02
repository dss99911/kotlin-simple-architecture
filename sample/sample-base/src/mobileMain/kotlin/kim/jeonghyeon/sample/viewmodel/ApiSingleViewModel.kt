package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.sample.di.serviceLocator

class ApiSingleViewModel(/*private val api: PreferenceApi*/) : BaseViewModel() {

    //todo required for ios to create instance, currently kotlin doesn't support predefined parameter
    // if it's supported, remove this
//    constructor() : this(serviceLocator.preferenceApi)

    private val KEY = "someKey"

    val result = dataFlow("")
    val input = dataFlow("")
        .withSource(result) { value = it }


    override fun onInitialized() {
        result.load(initStatus) {
//            client.get<String>("https://sample.jeonghyeon.kim/ping").also {
//                println(it)
//            }

            serviceLocator.preferenceApi.getString(KEY) ?: ""
        }
    }

    fun onClick() {
        result.load(status) {
            val text = input.value
            serviceLocator.preferenceApi.setString(KEY, text)
            text
        }
    }
}