package kim.jeonghyeon.net

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.HttpClientDsl
import io.ktor.client.features.ClientRequestException
import io.ktor.client.features.auth.Auth
import io.ktor.client.features.cookies.HttpCookies
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.HttpHeaders
import io.ktor.http.auth.HttpAuthHeader
import io.ktor.http.isSuccess
import io.ktor.network.sockets.SocketTimeoutException
import kim.jeonghyeon.auth.HEADER_NAME_TOKEN
import kim.jeonghyeon.net.error.ApiError
import kim.jeonghyeon.net.error.ApiErrorBody
import kim.jeonghyeon.net.error.errorApi
import kim.jeonghyeon.net.error.isApiError
import kim.jeonghyeon.pergist.Preference
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

@HttpClientDsl
expect fun httpClientSimple(config: HttpClientConfig<*>.() -> Unit = {}): HttpClient

expect fun Exception.isConnectException(): Boolean

@HttpClientDsl
fun httpClientDefault(config: HttpClientConfig<*>.() -> Unit = {}): HttpClient = HttpClient {
    install(JsonFeature) {
        serializer = KotlinxSerializer()
    }

    config()
}

suspend fun HttpClient.fetchResponseText(isAuthRequired: Boolean, call: suspend (HttpRequestBuilder.() -> Unit)-> HttpResponse): String {
    val response: HttpResponse
    val responseText: String
    try {
        response = call {
            if (isAuthRequired) {
                putTokenHeader()
            }
        }
        setResponse(response)
        responseText = response.readText()
    } catch (e: Exception) {
        throwException(e)
    }

    if (isAuthRequired) {
        response.saveToken()
    }

    validateResponse(response, responseText)
    return responseText
}

private fun throwException(e: Exception): Nothing {
    if (e.isConnectException()) {
        throw ApiError(ApiErrorBody.NoNetwork, e)
    }
    throw when (e) {
        //todo check what kind of network exception exists
        is SocketTimeoutException -> {
            ApiError(ApiErrorBody.NoNetwork, e)
        }
        is ClientRequestException -> {
            val status = e.response.status
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
private suspend fun validateResponse(response: HttpResponse, responseText: String) {
    //TODO HYUN [multi-platform2] : consider how to set header of response

    if (response.status.isApiError()) {
        val json = Json(JsonConfiguration.Stable)
        errorApi(json.parse(ApiErrorBody.serializer(), responseText))
    }

    if (response.status.isSuccess()) {
        return
    }

    errorApi(ApiErrorBody.CODE_UNKNOWN, "unknown error occurred : ${response.status}, Text : ${response.readText()}")
}

/**
 * todo make concrete logic or use some basic kotlin function
 */
fun String.isUri(): Boolean = contains("://")

infix fun String.connectPath(end: String): String {
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

fun HttpRequestBuilder.putTokenHeader() {
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