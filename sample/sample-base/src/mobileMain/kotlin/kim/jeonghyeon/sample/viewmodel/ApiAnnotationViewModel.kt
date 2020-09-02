package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.sample.api.Post
import kim.jeonghyeon.sample.api.SampleApi
import kim.jeonghyeon.sample.di.serviceLocator


class ApiAnnotationViewModel(private val api: SampleApi) : BaseViewModel() {

    //todo required for ios to create instance, currently kotlin doesn't support predefined parameter
    // if it's supported, remove this
    constructor() : this(serviceLocator.sampleApi)

    val result = dataFlow("")
    val input = dataFlow("")

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