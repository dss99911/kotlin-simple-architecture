package kim.jeonghyeon.api

import io.ktor.application.*
import kim.jeonghyeon.annotation.Api
import kim.jeonghyeon.annotation.ApiParameterType
import kim.jeonghyeon.di.application
import kim.jeonghyeon.jvm.extension.toJsonString
import kim.jeonghyeon.net.ApiBindingApi
import kim.jeonghyeon.net.ApiCallInfo
import kim.jeonghyeon.net.SimpleRouting
import kim.jeonghyeon.util.log
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.allSuperclasses
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.functions
import kotlin.reflect.jvm.javaType

/**
 * not support with sign api.(this is not easy as we have to controller pipeline application call.)
 */
class ApiBindingController : ApiBindingApi {
    override suspend fun call(apiCallInfos: List<ApiCallInfo>): List<String> {

        val responseList = mutableListOf<Any?>()
        apiCallInfos.forEach { apiCallInfo ->
            responseList.add(apiCallInfo.callApi(responseList))
        }

        return responseList.map {
            it.toJsonString()?: "null"//todo change to serialization instead of gson
        }
    }

    override suspend fun callWithAuth(apiCallInfos: List<ApiCallInfo>): List<String> =
        call(apiCallInfos)


    private suspend fun ApiCallInfo.callApi(responseList: List<Any?>): Any? {
        val controller = findController()

        val kFunction = controller.findFunction(functionName)
        log.i("[ApiBinding] $this")
        return kFunction.callSuspend(controller, *kFunction.makeParameter(this, responseList).toTypedArray())
    }

    private fun ApiCallInfo.findController(): Any {
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

    private fun KFunction<*>.makeParameter(apiCallInfo: ApiCallInfo, responseList: List<Any?>): List<Any?> = parameters
        .subList(1, parameters.size)
        .mapIndexed { index, param ->
            val parameterBinding = apiCallInfo.parameterBinding[index]
            if (parameterBinding != null) {
                getResponseDataFromBinding(parameterBinding, responseList)
            } else {
                val json = Json {}
                val parameterInfo = apiCallInfo.parameters[index]
                val jsonElement = if (parameterInfo.type == ApiParameterType.BODY || parameterInfo.type == ApiParameterType.NONE) {
                    parameterInfo.jsonElement
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
        val map = this as Map<String, Any?>
        val field = map[fieldNames[0]]
        return field?.getDataByFieldNames(fieldNames.subList(1, fieldNames.size))
    }
}