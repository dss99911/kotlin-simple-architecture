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
import kim.jeonghyeon.annotation.ApiParameterType
import kim.jeonghyeon.annotation.SimpleArchInternal
import kim.jeonghyeon.auth.HEADER_NAME_TOKEN
import kim.jeonghyeon.extension.toJsonString
import kim.jeonghyeon.net.error.*
import kim.jeonghyeon.net.error.isApiError
import kim.jeonghyeon.pergist.KEY_USER_TOKEN
import kim.jeonghyeon.pergist.Preference
import kim.jeonghyeon.pergist.getUserToken
import kim.jeonghyeon.pergist.removeUserToken
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
            throw ApiBindingException(callInfo, this)
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

    inline fun <reified T : Any?> T.toParameterString(): String? {
        return when (this) {
            is String -> this
            is Enum<*> -> this.name
            else -> this?.toJsonString()
        }
    }

    inline fun <reified  T : Any?> T.toJsonElement(): JsonElement =
        Json {}.encodeToJsonElement(this)

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
                if (status == HttpStatusCode.Unauthorized) {
                    /**
                     * todo if it's unauthorized on response, remove token on preference.
                     *  when remove token, check api url and realm.
                     */
                    Preference().removeUserToken()
                }
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

        if (response.status.isDeeplinkError()) {
            val json = Json { }
            errorDeeplink(json.decodeFromString(DeeplinkInfo.serializer(), responseText))
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

        val tokenString = Preference().getUserToken()
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
        Preference().setEncryptedString(Preference.KEY_USER_TOKEN, tokenString)
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
                replacedSubPath.replace("{${it.key}}", it.value.toString())

            }
            replacedSubPath
        }
    }

    fun body(): JsonElement? {
        parameters.firstOrNull { it.type == ApiParameterType.BODY }?.jsonElement?.let {
            return it
        }

        val bodyParameters = parameters.filter { it.type == ApiParameterType.NONE }
        if (bodyParameters.isEmpty()) {
            return null
        }

        return buildJsonObject {
            bodyParameters.forEach {
                put(it.parameterName, it.jsonElement!!)
            }
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
 * @param jsonElement if it's [ApiParameterType.BODY], [ApiParameterType.NONE] type, this exists.
 */
@Serializable
data class ApiParameterInfo(
    val type: ApiParameterType,
    val parameterName: String,//todo this seems not required
    val key: String?,
    val value: String?,
    val jsonElement: JsonElement?
)


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
