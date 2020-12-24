package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.client.*
import kim.jeonghyeon.sample.api.AnnotationAction
import kim.jeonghyeon.sample.api.AnnotationObject
import kim.jeonghyeon.sample.api.SampleApi
import kim.jeonghyeon.sample.di.serviceLocator
import kim.jeonghyeon.type.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

/**
 * This shows how to customize api call by annotation
 * please check [SampleApi.getAnnotation], [SampleApi.putAnnotation]
 * there is no meaningful logic on request and response
 *
 * If you want to know how to call external api. refer [ApiExternalViewModel]
 */
class ApiAnnotationViewModel(private val api: SampleApi = serviceLocator.sampleApi) : ModelViewModel() {
    //todo [KSA-48] support localization on kotlin side
    override val title: String = "Annotation Api call"
    private val obj = viewModelFlow<AnnotationObject>()
    val result = obj.map { it.toString() }.toData()
    val input = viewModelFlow<String>()

    override fun onInit() {
        obj.load(initStatus) {
            api.getAnnotation("key1", AnnotationAction.QUERY, "header3")
        }
    }

    fun onClick() {
        obj.loadInIdle(status) {
            api.putAnnotation(input.valueOrNull ?: error("input key"), obj.value)
        }
    }
}

// TODO reactive way.
//class ApiAnnotationViewModel2(private val api: SampleApi = serviceLocator.sampleApi) : ModelViewModel() {
//    //todo [KSA-48] support localization on kotlin side
//    override val title: String = "Annotation Api call"
//
//    //event
//    val click = viewModelFlow<Unit>()
//    val input by add { viewModelFlow<String>() }
//
//
//    @OptIn(ExperimentalCoroutinesApi::class)
//    val result: SharedFlow<AnnotationObject> by add {
//        merge(
//            initFlow
//                .map { api.getAnnotation("key1", AnnotationAction.QUERY, "header3") }
//                .toData(initStatus),
//            click
//                .mapInIdle {
//                    api.putAnnotation(input.valueOrNull ?: error("input key"), result.value)
//                }
//                .toData(status)
//        ).toData()
//    }
//
//
//
//}