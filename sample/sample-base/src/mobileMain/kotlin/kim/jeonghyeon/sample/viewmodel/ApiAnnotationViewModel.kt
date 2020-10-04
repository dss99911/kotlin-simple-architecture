package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.client.DataFlow
import kim.jeonghyeon.sample.api.Post
import kim.jeonghyeon.sample.api.SampleApi
import kim.jeonghyeon.sample.di.serviceLocator


class ApiAnnotationViewModel(private val api: SampleApi) : SampleViewModel() {

    //todo required for ios to create instance, currently kotlin doesn't support predefined parameter
    // if it's supported, remove this
    constructor() : this(serviceLocator.sampleApi)

    val result by add { DataFlow<String>() }
    val input by add { DataFlow<String>() }

    override fun onInit() {
        result.load(initStatus) {
            api.getAnnotation("idvalue", "actionvalue", "authvalue").toString()
        }
    }

    fun onClick() {
        result.load(status) {
            api.putAnnotation("idvalue", Post(0,input.value?:""))
            "success"
        }
    }
}