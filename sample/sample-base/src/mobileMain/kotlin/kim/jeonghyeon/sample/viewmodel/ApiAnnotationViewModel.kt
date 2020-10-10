package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.client.DataFlow
import kim.jeonghyeon.sample.api.Pair2
import kim.jeonghyeon.sample.api.AnnotationAction
import kim.jeonghyeon.sample.api.SampleApi
import kim.jeonghyeon.sample.di.serviceLocator
import kim.jeonghyeon.sample.api.AnnotationObject
import kotlinx.coroutines.flow.map

/**
 * This shows how to customize api call by annotation
 * please check [SampleApi.getAnnotation], [SampleApi.putAnnotation]
 * there is no meaningful logic on request and response
 *
 * If you want to know how to call external api. refer [ApiExternalViewModel]
 */
class ApiAnnotationViewModel(private val api: SampleApi) : SampleViewModel() {

    //todo required for ios to create instance, currently kotlin doesn't support predefined parameter
    // if it's supported, remove this
    constructor() : this(serviceLocator.sampleApi)

    private val obj by add { DataFlow<AnnotationObject>() }
    val result by add { obj.map { it.toString() }.toDataFlow() }
    val input by add { DataFlow<String>() }

    override fun onInit() {
        obj.load(initStatus) {
            api.getAnnotation("key1", AnnotationAction.QUERY, "header3")
        }
    }

    fun onClick() {
        obj.load(status) {
            api.putAnnotation("key2", AnnotationObject("key2", Pair2(AnnotationAction.INSERT, input.value?: error("input value"))))
        }
    }
}