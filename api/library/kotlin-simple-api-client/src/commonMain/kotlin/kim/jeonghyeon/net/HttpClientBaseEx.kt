package kim.jeonghyeon.net

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.content.*
import io.ktor.http.*
import kim.jeonghyeon.annotation.ApiParameterType
import kim.jeonghyeon.annotation.SimpleArchInternal
import kim.jeonghyeon.net.SimpleApiUtil.isKotlinXSerializer
import kim.jeonghyeon.net.SimpleApiUtil.toJsonElement
import kim.jeonghyeon.net.error.ApiError
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement


expect fun Throwable.isConnectException(): Boolean

@SimpleArchInternal
object SimpleApiUtil {
    @OptIn(ExperimentalStdlibApi::class)
    suspend inline fun <reified RET> HttpClient.callApi(
        callInfo: ApiCallInfo,
        requestResponseAdapter: RequestResponseAdapter = getDefaultRequestResponseAdapter()
    ): RET {
        requestResponseAdapter.beforeBuildRequest(callInfo, this)

        var response: HttpResponse? = null
        try {
            response = requestApi(callInfo, requestResponseAdapter)
            val returnValue = requestResponseAdapter.transformResponse<RET>(response, callInfo, typeInfo<RET>())
            setResponse(response)//`freeze` error occurs if call before readText()
            return returnValue
        } catch (e: Exception) {
            response?.let { setResponse(it) }
            if (e is ApiError || e is DeeplinkError) {
                throw e
            }
            return requestResponseAdapter.handleException(e, callInfo, typeInfo<RET>())
        }


    }

    inline fun <reified T : Any?> T.toParameterString(client: HttpClient): String? {
        if (this == null) {
            return null
        }

        return when (this) {
            is String -> this
            is Enum<*> -> this.name
            else -> {
                if (client.isKotlinXSerializer()) {
                    Json {}.encodeToString(this)//with below, generic type has error, so, use this way.
                } else {
                    (client.feature(JsonFeature)!!.serializer.write(this) as TextContent).text
                }

            }
        }
    }

    inline fun <reified T> T.toJsonElement(client: HttpClient): JsonElement? =
        if (client.isKotlinXSerializer()) {
            Json {}.encodeToJsonElement(this)
        } else {
            null
        }

    suspend fun HttpClient.requestApi(
        callInfo: ApiCallInfo,
        requestResponseAdapter: RequestResponseAdapter
    ) = request<HttpResponse> {
        url.takeFrom(callInfo.buildPath())

        method = callInfo.method

        callInfo.body(this@requestApi)?.let { body = it }
        if (callInfo.isJsonContentType()) {
            contentType(ContentType.Application.Json)
        }
        callInfo.queries().forEach {
            parameter(it.first, it.second)

        }
        callInfo.headers().forEach {
            header(it.first, it.second)
        }

        requestResponseAdapter.buildRequest(this, callInfo)
    }

    fun HttpClient.isKotlinXSerializer(): Boolean {
        return feature(JsonFeature)!!.serializer::class == KotlinxSerializer::class
    }

}

/**
 * todo make concrete logic or use some basic kotlin function
 */
fun String.isUri(): Boolean = contains("://")

data class ApiCallInfo(
    val baseUrl: String,
    val mainPath: String,
    val subPath: String,
    val className: String,//if there is no customization, className and mainPath is same
    val functionName: String,//if there is no customization, subPath and functionName is same
    val method: HttpMethod,
    val isAuthRequired: Boolean,
    val parameters: List<ApiParameterInfo>,
    val parameterBinding: Map<Int, String> = emptyMap()
) {
    fun buildPath(): String {
        return baseUrl connectPath mainPath connectPath subPath.let {
            var replacedSubPath = it
            parameters.filter { it.type == ApiParameterType.PATH }.forEach {
                replacedSubPath = replacedSubPath.replace("{${it.key}}", it.value.toString())

            }
            replacedSubPath
        }
    }

    @OptIn(SimpleArchInternal::class)
    fun body(client: HttpClient): Any? {
        parameters.firstOrNull { it.type == ApiParameterType.BODY }?.getBodyOrJsonElement()?.let {
            return it
        }

        val bodyParameters = parameters.filter { it.type == ApiParameterType.NONE }
        if (bodyParameters.isEmpty()) {
            return null
        }

        return if (client.isKotlinXSerializer()) {
            buildJsonObject {
                bodyParameters.forEach {
                    put(it.parameterName, (it.bodyJsonElement?:return@forEach))
                }
            }
        } else {
            bodyParameters.associate { Pair(it.parameterName, it.body) }
        }
    }

    fun isJsonContentType() = when (method) {
        HttpMethod.Post, HttpMethod.Put, HttpMethod.Delete, HttpMethod.Patch -> true
        else -> false
    }

    fun queries() = parameters.filter { it.type == ApiParameterType.QUERY }
        .map { it.key!! to it.value }

    fun headers() = parameters.filter { it.type == ApiParameterType.HEADER }
        .map { it.key!! to it.value }


    private infix fun String.connectPath(end: String): String {
        if (isEmpty() || end.isEmpty()) return this + end

        if (end.isUri()) {
            return end
        }

        return if (last() == '/' && end.first() == '/') {
            take(lastIndex) + end
        } else if (last() != '/' && end.first() != '/') {
            "$this/$end"
        } else this + end
    }
}


/**
 * @param key if it's [ApiParameterType.BODY], [ApiParameterType.NONE] type, there is no key
 * @param value if it's [ApiParameterType.BODY], [ApiParameterType.NONE] type, this is null
 * @param body if it's [ApiParameterType.BODY], [ApiParameterType.NONE] type, this exists.
 */
data class ApiParameterInfo(
    val type: ApiParameterType,
    val parameterName: String,//todo this seems not required
    val key: String?,
    val value: String?,
    val body: Any?,
    val bodyJsonElement: JsonElement?,
) {
    fun getBodyOrJsonElement(): Any? {
        return body?:bodyJsonElement
    }
}