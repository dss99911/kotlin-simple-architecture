package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.sample.api.Post
import kim.jeonghyeon.sample.di.serviceLocator


class ApiAnnotationViewModel(/*private val api: SampleApi*/) : BaseViewModel() {

    //todo required for ios to create instance, currently kotlin doesn't support predefined parameter
    // if it's supported, remove this
//    constructor() : this(serviceLocator.sampleApi)

    val result = dataFlow("")
    val input = dataFlow("")

    override fun onInitialized() {
        result.load(initStatus) {
            //todo after this fixed https://youtrack.jetbrains.com/issue/KTOR-973
            // use constructor parameter
            serviceLocator.sampleApi.getAnnotation("idvalue", "actionvalue", "authvalue").toString()
        }
    }

    fun onClick() {
        result.load(status) {
            //todo after this fixed https://youtrack.jetbrains.com/issue/KTOR-973
            // use constructor parameter
            serviceLocator.sampleApi.putAnnotation("idvalue", Post(0,input.value))
            "success"
        }
    }
}