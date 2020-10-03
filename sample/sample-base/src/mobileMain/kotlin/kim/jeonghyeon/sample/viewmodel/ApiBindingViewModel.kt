@file:UseContextualSerialization
package kim.jeonghyeon.sample.viewmodel

import io.ktor.client.*
import io.ktor.client.features.json.serializer.*
import io.ktor.content.*
import io.ktor.http.content.TextContent
import kim.jeonghyeon.annotation.ApiParameterType
import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.extension.toJsonString
import kim.jeonghyeon.net.ApiParameterInfo
import kim.jeonghyeon.net.bindApi
import kim.jeonghyeon.net.client
import kim.jeonghyeon.sample.api.Post
import kim.jeonghyeon.sample.api.SampleApi
import kim.jeonghyeon.sample.api.UserApi
import kim.jeonghyeon.sample.di.serviceLocator
import kim.jeonghyeon.sample.repository.UserRepository
import kim.jeonghyeon.util.log
import kotlinx.serialization.*
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.serializersModule
import kotlinx.serialization.modules.serializersModuleOf

class ApiBindingViewModel(val api: SampleApi, val userApi: UserApi) : SampleViewModel() {
    override val signInRequired: Boolean = false

    //todo required for ios to create instance, currently kotlin doesn't support predefined parameter
    // if it's supported, remove this
    constructor(): this(serviceLocator.sampleApi, serviceLocator.userApi)

    @OptIn(InternalSerializationApi::class)
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