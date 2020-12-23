package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.client.flowSingle
import kim.jeonghyeon.client.flowViewModel
import kim.jeonghyeon.client.toData
import kim.jeonghyeon.net.bindApi
import kim.jeonghyeon.sample.api.AnnotationAction
import kim.jeonghyeon.sample.api.SampleApi
import kim.jeonghyeon.sample.api.UserApi
import kim.jeonghyeon.sample.di.serviceLocator
import kim.jeonghyeon.util.log
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

class ApiBindingViewModel(val api: SampleApi = serviceLocator.sampleApi, val userApi: UserApi = serviceLocator.userApi) : ModelViewModel() {

    //todo [KSA-48] support localization on kotlin side
    override val title: String = "Api Binding"

    val result by add { flowViewModel<String>() }

    override fun onInit() {
        result.load(initStatus) {
            api.getIncreasedNumber().toString()
        }
    }

    fun onClickBind2Api() {
        result.loadInIdle(status) {
            bindApi {
                api.getIncreasedNumber()
            }.bindApi {
                api.getIncreasedNumber()
            }.execute().toString()
        }
    }

    fun onClickBind3Api() {
        result.loadInIdle(status) {
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
        result.loadInIdle(status) {
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
        result.loadInIdle(status) {
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
        result.loadInIdle(status) {
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
        result.loadInIdle(status) {
            bindApi {
                api.getIncreasedNumber()
            }.bindApi {
                userApi.getUser()
            }.execute().toString()
        }
    }
}

class ApiBindingViewModel2(val api: SampleApi = serviceLocator.sampleApi, val userApi: UserApi = serviceLocator.userApi) : ModelViewModel() {

    //todo [KSA-48] support localization on kotlin side
    override val title: String = "Api Binding"

    val clickBind2Api = flowViewModel<Unit>()
    val clickBind3Api = flowViewModel<Unit>()
    val clickBindResposneToParameter = flowViewModel<Unit>()
    val clickBindResposneFieldToParameter = flowViewModel<Unit>()
    val clickHandleError = flowViewModel<Unit>()
    val clickBindApiAuthRequired = flowViewModel<Unit>()

    val result by add {
        merge(
            initFlow.mapInIdle {
                api.getIncreasedNumber().toString()
            }.toData(scope, initStatus),

            merge(
                clickBind2Api.mapInIdle {
                    bindApi {
                        api.getIncreasedNumber()
                    }.bindApi {
                        api.getIncreasedNumber()
                    }.execute().toString()
                },
                clickBind3Api.mapInIdle {
                    bindApi {
                        api.getIncreasedNumber()
                    }.bindApi {
                        api.getIncreasedNumber()
                    }.bindApi { _, _ ->
                        api.getIncreasedNumber()
                    }.execute().toString()
                },
                clickBindResposneToParameter.mapInIdle {
                    bindApi {
                        api.getIncreasedNumber()
                    }.bindApi {
                        api.getIncreasedNumber()

                    }.bindApi { first, second ->
                        api.minus(first.bindParameter(0), second.bindParameter(1))
                    }.execute().toString()
                },
                clickBindResposneFieldToParameter.mapInIdle {
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
                },
                clickHandleError.mapInIdle {
                    bindApi {
                        api.getSuccess()
                    }.bindApi {
                        //bind field
                        api.getRandomError(2)
                    }.bindApi { _, _ ->
                        //bind field's field
                        api.getSuccess()
                    }.execute().toString()
                },
                clickBindApiAuthRequired.mapInIdle {
                    bindApi {
                        api.getIncreasedNumber()
                    }.bindApi {
                        userApi.getUser()
                    }.execute().toString()
                }
            )
            .toData(scope, status)
        )
    }
}