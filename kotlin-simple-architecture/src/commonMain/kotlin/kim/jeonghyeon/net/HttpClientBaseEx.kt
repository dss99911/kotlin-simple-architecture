@file:UseSerializers(HttpMethodSerializer::class)

package kim.jeonghyeon.net

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.auth.*
import io.ktor.network.sockets.*
import kim.jeonghyeon.annotation.SimpleArchInternal
import kim.jeonghyeon.auth.HEADER_NAME_TOKEN
import kim.jeonghyeon.extension.toJsonString
import kim.jeonghyeon.net.error.ApiError
import kim.jeonghyeon.net.error.ApiErrorBody
import kim.jeonghyeon.net.error.errorApi
import kim.jeonghyeon.net.error.isApiError
import kim.jeonghyeon.pergist.Preference
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json


@HttpClientDsl
expect fun httpClientSimple(config: HttpClientConfig<*>.() -> Unit = {}): HttpClient

expect fun Throwable.isConnectException(): Boolean

@HttpClientDsl
fun httpClientDefault(config: HttpClientConfig<*>.() -> Unit = {}): HttpClient = HttpClient {
    install(JsonFeature) {
        serializer = KotlinxSerializer()
    }

    config()
}

@SimpleArchInternal
object SimpleApiUtil {
    suspend inline fun <reified RET> HttpClient.callApi(callInfo: ApiCallInfo): RET {
        if (isApiBinding()) {
            throw ApiBindingException(callInfo)
        }
        var response: HttpResponse? = null
        val responseText: String

        try {
            response = requestApi(callInfo)
            responseText = response.readText()
            setResponse(response)//`freeze` error occurs if call before readText()
        } catch (e: Exception) {
            response?.let { setResponse(it) }
            throwException(e)
        }

        if (callInfo.isAuthRequired) {
            response.saveToken()
        }

        validateResponse(response, responseText)

        if (RET::class == Unit::class) {
            return Unit as RET
        }
        return Json { ignoreUnknownKeys = true }.decodeFromString(serializer(), responseText)
    }

    suspend fun HttpClient.requestApi(callInfo: ApiCallInfo) = request<HttpResponse> {
        url.takeFrom(callInfo.buildPath())
        method = callInfo.method
        callInfo.body()?.let { body = it }
        if (callInfo.isJsonContentType()) {
            contentType(ContentType.Application.Json)
        }
        callInfo.queries().forEach {
            parameter(it.first, it.second)
        }
        callInfo.headers().forEach {
            header(it.first, it.second)
        }
        if (callInfo.isAuthRequired) {
            putTokenHeader()
        }
    }

    inline fun <reified T : Any> convertParameter(parameter: T?): String? {
        return when (parameter) {
            is String -> parameter
            is Enum<*> -> parameter.name
            else -> parameter?.toJsonString()
        }
    }

    fun throwException(e: Throwable): Nothing {
        if (e.isConnectException()) {
            throw ApiError(ApiErrorBody.NoNetwork, e)
        }
        throw when (e) {
            //todo check what kind of network exception exists
            is SocketTimeoutException -> {
                ApiError(ApiErrorBody.NoNetwork, e)
            }
            is ClientRequestException -> {
                val status = e.response!!.status
                ApiError(ApiErrorBody(status.value, status.description), e)
            }
            else -> {
                ApiError(ApiErrorBody.Unknown, e)
            }
        }
    }

    /**
     * @throws ApiError if error
     * @return if success
     */
    suspend fun validateResponse(response: HttpResponse, responseText: String) {
        if (response.status.isApiError()) {
            val json = Json { }
            errorApi(json.decodeFromString(ApiErrorBody.serializer(), responseText))
        }

        if (response.status.isSuccess()) {
            return
        }

        errorApi(ApiErrorBody.CODE_UNKNOWN, "unknown error occurred : ${response.status}, Text : ${response.readText()}")
    }

    private fun HttpRequestBuilder.putTokenHeader() {
        //if it's signin, no token required
        if (headers[HttpHeaders.Authorization] != null) {
            return
        }

        val tokenString = Preference().getEncryptedString(HEADER_NAME_TOKEN)
        if (tokenString.isNullOrEmpty()) {
            return
        }
        //for stateful session based authentication
        header(HEADER_NAME_TOKEN, tokenString)

        //for jwt token authentication
        header(HttpHeaders.Authorization, HttpAuthHeader.Single("Bearer", tokenString).render())

    }

    fun HttpResponse.saveToken() {
        //if sigh out, tokenString will be ""
        val tokenString = headers[HEADER_NAME_TOKEN]?:return
        Preference().setEncryptedString(HEADER_NAME_TOKEN, tokenString)
    }
}

/**
 * todo make concrete logic or use some basic kotlin function
 */
internal fun String.isUri(): Boolean = contains("://")

@Serializable
data class ApiCallInfo(
    val baseUrl: String,
    val mainPath: String,
    val subPath: String,
    val method: HttpMethod,
    val isAuthRequired: Boolean,
    val parameters: List<ApiParameterInfo>,
    val parameterBinding: Map<Int, String> = emptyMap()
) {
    fun buildPath(): String {
        return baseUrl connectPath mainPath connectPath subPath.let {
            var replacedSubPath = it
            parameters.filter { it.type == ApiParameterType.PATH }.forEach {
                replacedSubPath.replace("{${it.key}}", it.value.toString())

            }
            replacedSubPath
        }
    }

    fun body(): Any? = parameters.firstOrNull { it.type == ApiParameterType.BODY }?.value

    fun isJsonContentType() = when (method) {
        HttpMethod.Post, HttpMethod.Put, HttpMethod.Delete, HttpMethod.Patch -> true
        else -> false
    }
    fun queries() = parameters.filter { it.type == ApiParameterType.QUERY }
            .map { it.key!! to it.value.toString() }
    fun headers() = parameters.filter { it.type == ApiParameterType.HEADER }
        .map { it.key!! to it.value.toString() }


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

@Serializer(forClass = HttpMethod::class)
internal object HttpMethodSerializer : KSerializer<HttpMethod> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("HttpMethodDefaultSerializer", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): HttpMethod {
        return HttpMethod(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: HttpMethod) {
        encoder.encodeString(value.value)
    }
}
