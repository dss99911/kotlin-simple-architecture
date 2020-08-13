package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.sample.api.Post
import kim.jeonghyeon.sample.api.SampleApi
import kim.jeonghyeon.sample.di.serviceLocator
import kotlinx.coroutines.flow.MutableStateFlow

class ApiAnnotationViewModel(private val api: SampleApi = serviceLocator.sampleApi) :
    BaseViewModel() {

    val result = MutableStateFlow("")
    val input = MutableStateFlow("")
    private lateinit var post: Post

    override fun onInitialized() {
        result.load(initStatus) {
            api.getAnnotation("idvalue", "actionvalue", "authvalue")
                .also { post = it.first }
                .toString()
        }
    }

    fun onClick() {
        result.load(status) {
            api.putAnnotation("idvalue", post)
            "success"
        }
    }
}