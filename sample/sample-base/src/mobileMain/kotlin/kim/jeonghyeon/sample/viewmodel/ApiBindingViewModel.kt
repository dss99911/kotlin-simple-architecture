package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.net.bindApi
import kim.jeonghyeon.sample.api.SampleApi
import kim.jeonghyeon.sample.api.UserApi
import kim.jeonghyeon.sample.di.serviceLocator
import kim.jeonghyeon.util.log

class ApiBindingViewModel(val api: SampleApi, val userApi: UserApi) : SampleViewModel() {
    override val signInRequired: Boolean = false

    //todo required for ios to create instance, currently kotlin doesn't support predefined parameter
    // if it's supported, remove this
    constructor() : this(serviceLocator.sampleApi, serviceLocator.userApi)

    override fun onInit() {
        initStatus.load {
            val result = bindApi {
                api.getWords()
            }.bindApi {
                userApi.getUser()
            }.bindApi { data1, _ ->
                api.addWords(data1.bindParameter(0))
            }.execute()

            log.i("result is $result")
        }
    }
}