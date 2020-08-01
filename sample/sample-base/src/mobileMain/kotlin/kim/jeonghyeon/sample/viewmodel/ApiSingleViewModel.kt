package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.api.PreferenceApi
import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.sample.di.serviceLocator
import kotlinx.coroutines.flow.MutableStateFlow

class ApiSingleViewModel(private val api: PreferenceApi = serviceLocator.preferenceApi) : BaseViewModel() {
    private val KEY = "someKey"

    val result = MutableStateFlow("")
    val input = MutableStateFlow("")
        .withSource(result) { value = it }


    override fun onInitialized() {
        result.load(initStatus) {
            api.getString(KEY) ?: ""
        }
    }

    fun onClick() {
        result.load(status) {
            val text = input.value
            api.setString(KEY, text)
            text
        }
    }
}