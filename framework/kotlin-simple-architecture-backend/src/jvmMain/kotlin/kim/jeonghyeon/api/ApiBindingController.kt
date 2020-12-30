package kim.jeonghyeon.api

import io.ktor.application.*
import kim.jeonghyeon.annotation.Api
import kim.jeonghyeon.annotation.ApiParameterType
import kim.jeonghyeon.annotation.SimpleArchInternal
import kim.jeonghyeon.di.application
import kim.jeonghyeon.extension.toJsonString
import kim.jeonghyeon.net.ApiBindingApi
import kim.jeonghyeon.net.ApiBindingCallInfo
import kim.jeonghyeon.net.SimpleRouting
import kim.jeonghyeon.net.error.ApiError
import kim.jeonghyeon.net.error.ApiErrorBody
import kim.jeonghyeon.net.error.errorApi
import kim.jeonghyeon.util.log
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import java.lang.reflect.InvocationTargetException
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.allSuperclasses
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.functions
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaType

/**
 * not support with sign api.(this is not easy as we have to controller pipeline application call.)
 */
class ApiBindingController : ApiBindingApi {
    override suspend fun call(apiCallInfos: List<ApiBindingCallInfo>): List<String> {

        val responseList = mutableListOf<Any?>()
        try {
            apiCallInfos.forEach { apiCallInfo ->
                responseList.add(apiCallInfo.callApi(responseList))
            }
        } catch (e: InvocationTargetException) {
            //todo deliver success data as well.
            val targetException = e.targetException
            if (targetException is ApiError) {
                errorApi(targetException.body)
            } else {
                errorApi(ApiErrorBody.Unknown.code, targetException.message)
            }

        }

        return responseList.map {
            it.toJsonString()?: "null"//todo change to serialization instead of gson
        }
    }

    override suspend fun callWithAuth(apiCallInfos: List<ApiBindingCallInfo>): List<String> =
        call(apiCallInfos)


    private suspend fun ApiBindingCallInfo.callApi(responseList: List<Any?>): Any? {
        val controller = findController()

        val kFunction = controller.findFunction(functionName)
        log.i("[ApiBinding] $this")
        return kFunction.callSuspend(controller, *kFunction.makeParameter(this, responseList).toTypedArray())
    }

    @OptIn(SimpleArchInternal::class)
    private fun ApiBindingCallInfo.findController(): Any {
        val routing = application.feature(SimpleRouting)
        val controllerList = routing.config.controllerList

        return controllerList.firstOrNull {
            it::class.allSuperclasses
                .filter { it.getApiAnnotation() != null }
                .any { it.qualifiedName == className }
        } ?: error("controller not exists for $className")

    }

    private fun KClass<*>.getApiAnnotation(): Api? =
        annotations.firstOrNull { annotation -> annotation is Api } as? Api?

    /**
     * not support multiple same function name. (todo add type on request. and support this)
     */
    private fun Any.findFunction(funcName: String): KFunction<*> =
        this::class.functions.first { funcName == it.name }

    private fun KFunction<*>.makeParameter(apiCallInfo: ApiBindingCallInfo, responseList: List<Any?>): List<Any?> = parameters
        .subList(1, parameters.size)
        .mapIndexed { index, param ->
            val parameterBinding = apiCallInfo.parameterBinding[index]
            if (parameterBinding != null) {
                getResponseDataFromBinding(parameterBinding, responseList)
            } else {
                val json = Json {}
                val parameterInfo = apiCallInfo.parameters[index]
                val jsonElement = if (parameterInfo.type == ApiParameterType.BODY || parameterInfo.type == ApiParameterType.NONE) {
                    parameterInfo.body
                } else json.encodeToJsonElement(String.serializer(), parameterInfo.value.toString())
                jsonElement?.let { json.decodeFromJsonElement(serializer(param.type), it) }
            }
        }

    private fun getResponseDataFromBinding(parameterBinding: String, responseList: List<Any?>): Any? {
        val fieldNames = parameterBinding.split(".")
        return responseList[fieldNames[0].toInt()]?.getDataByFieldNames(fieldNames.subList(1, fieldNames.size))
    }

    private fun Any.getDataByFieldNames(fieldNames: List<String>): Any? {
        if (fieldNames.isEmpty()) {
            return this
        }


        @Suppress("UNCHECKED_CAST")
        val field = this::class.java
            .getDeclaredField(fieldNames[0])
            .apply {
                isAccessible = true
            }
            .get(this)
        return field?.getDataByFieldNames(fieldNames.subList(1, fieldNames.size))
    }
}