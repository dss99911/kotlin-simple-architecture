package kim.jeonghyeon.api

import io.ktor.application.*
import kim.jeonghyeon.annotation.Api
import kim.jeonghyeon.di.application
import kim.jeonghyeon.jvm.extension.toJsonString
import kim.jeonghyeon.net.ApiBindingApi
import kim.jeonghyeon.net.ApiCallInfo
import kim.jeonghyeon.net.SimpleRouting
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.allSuperclasses
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.functions

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
            it.toJsonString()?: "null"
        }
    }

    override suspend fun callWithAuth(apiCallInfos: List<ApiCallInfo>): List<String> =
        call(apiCallInfos)


    private suspend fun ApiCallInfo.callApi(responseList: List<Any?>): Any? {
        val controller = findController()

        val kFunction = controller.findFunction(subPath)

        return kFunction.callSuspend(controller, *kFunction.makeParameter(this, responseList).toTypedArray())
    }

    private fun ApiCallInfo.findController(): Any {
        val routing = application.feature(SimpleRouting)
        val controllerList = routing.config.controllerList
        val classPath = mainPath.replace("/", ".")

        return controllerList.firstOrNull {
            it::class.allSuperclasses
                .filter { it.getApiAnnotation() != null }
                .any { it.qualifiedName == classPath }
        } ?: error("controller not exists for $classPath")

    }

    private fun KClass<*>.getApiAnnotation(): Api? =
        annotations.firstOrNull { annotation -> annotation is Api } as? Api?

    /**
     * not support multiple same function name. (todo add type on request. and support this)
     */
    private fun Any.findFunction(funcName: String): KFunction<*> =
        this::class.functions.first { funcName == it.name }

    /**
     * support only parameter body.(todo support other type, even if support other type. we have to focus on kotlin interface instead of api request(api url, body, query, path etc..))
     */
    private fun KFunction<*>.makeParameter(apiCallInfo: ApiCallInfo, responseList: List<Any?>): List<Any?> {
        //if need to replace previous response. do it.
        val map = apiCallInfo.parameters[0].value as Map<String, Any?>
        return parameters.mapIndexed { index, param ->
            val parameterBinding = apiCallInfo.parameterBinding[index]
            if (parameterBinding != null) {
                getResponseDataFromBinding(parameterBinding, responseList)
            } else {
                map.entries.first { it.key == param.name }.value
            }
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