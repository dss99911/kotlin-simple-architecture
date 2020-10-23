package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.client.DataFlow
import kim.jeonghyeon.net.bindApi
import kim.jeonghyeon.sample.api.AnnotationAction
import kim.jeonghyeon.sample.api.SampleApi
import kim.jeonghyeon.sample.api.UserApi
import kim.jeonghyeon.sample.di.serviceLocator
import kim.jeonghyeon.util.log
import kotlinx.coroutines.flow.map

class ApiBindingViewModel(val api: SampleApi = serviceLocator.sampleApi, val userApi: UserApi = serviceLocator.userApi) : ModelViewModel() {

    //todo [KSA-48] support localization on kotlin side
    override val title: String = "Api Binding"

    val result by add { DataFlow<String>() }

    override fun onInit() {
        result.load(initStatus) {
            api.getIncreasedNumber().toString()
        }
    }

    fun onClickBind2Api() {
        result.load(status) {
            bindApi {
                api.getIncreasedNumber()
            }.bindApi {
                api.getIncreasedNumber()
            }.execute().toString()
        }
    }

    fun onClickBind3Api() {
        result.load(status) {
            bindApi {
                api.getIncreasedNumber()
            }.bindApi {
                api.getIncreasedNumber()
            }.bindApi { _, _ ->
                api.getIncreasedNumber()
            }.execute().toString()
        }
    }

    fun onClickBindResposneToParameter() {
        result.load(status) {
            bindApi {
                api.getIncreasedNumber()
            }.bindApi {
                api.getIncreasedNumber()

            }.bindApi { first, second ->
                api.minus(first.bindParameter(0), second.bindParameter(1))
            }.execute().toString()
        }
    }

    fun onClickBindResposneFieldToParameter() {
        result.load(status) {
            bindApi {
                api.getAnnotation("key1", AnnotationAction.QUERY, "header")
            }.bindApi {
                //bind field
                api.repeat(it.bindParameter(0) { response ->
                    response::key
                }, 2)
            }.bindApi { first, _ ->
                //bind field's field
                api.repeat(first.bindParameter(0) { response ->
                    val bind = response::data.bind()
                    bind::second
                }, 2)
            }.execute().toString()
        }
    }

    /**
     * todo https://hyun.myjetbrains.com/youtrack/issue/KSA-138
     *  currently just return error. but it'll be possible to return previous success data when middle api occur error
     *  so, client decide to retry from middle or from first
     */
    fun onClickHandleError() {
        result.load(status) {
            bindApi {
                api.getSuccess()
            }.bindApi {
                //bind field
                api.getRandomError(2)
            }.bindApi { _, _ ->
                //bind field's field
                api.getSuccess()
            }.execute().toString()
        }
    }

    fun onClickBindApiAuthRequired() {
        result.load(status) {
            bindApi {
                api.getIncreasedNumber()
            }.bindApi {
                userApi.getUser()
            }.execute().toString()
        }
    }

}