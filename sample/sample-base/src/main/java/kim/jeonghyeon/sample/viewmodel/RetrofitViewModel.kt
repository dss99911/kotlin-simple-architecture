package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.api.PreferenceApi
import kim.jeonghyeon.sample.api.*
import kim.jeonghyeon.sample.di.serviceLocator
import kim.jeonghyeon.util.log

data class RetrofitViewModel(private val api: RetrofitApi = getRetrofitApi(), private val apiFromSimpleApi: RetrofitApi = getRetrofitApiFromSimpleApi()) : ModelViewModel() {

    //todo [KSA-48] support localization on kotlin side
    override val title: String = "Retrofit View Model"

    private val KEY = "someKey"

    val result = viewModelFlow<String>()

    fun onClickRetrofit() {
        result.load(status) {
            api.getValue("a").let {
                "${it}"
            } + "\n" + api.setValue("a", RetrofitRequestBody("dd")).toString()
        }
    }

    fun onClickSimpleApi() {
        result.load(status) {
            apiFromSimpleApi.getValue("a").let {
                "${it}"
            } + "\n" + api.setValue("a", RetrofitRequestBody("dd")).toString()
        }
    }
}