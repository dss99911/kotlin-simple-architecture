package kim.jeonghyeon.common.net

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.isSuccess
import io.ktor.network.sockets.SocketTimeoutException
import kim.jeonghyeon.common.net.error.ApiError
import kim.jeonghyeon.common.net.error.ApiErrorBody
import kim.jeonghyeon.common.net.error.ApiErrorCode
import kim.jeonghyeon.common.net.error.isApiError
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

fun httpClientDefault(config: HttpClientConfig<*>.() -> Unit = {}): HttpClient = HttpClient {
    install(JsonFeature) {
        serializer = KotlinxSerializer()
    }

    config(this)
}

fun HttpClient.throwException(e: Exception): Nothing {
    throw when (e) {
        //todo check what kind of network exception exists
        is SocketTimeoutException -> {
            ApiError(ApiErrorBody(ApiErrorCode.NO_NETWORK, "no network"), e)
        }
        else -> {
            ApiError(ApiErrorBody(ApiErrorCode.UNKNOWN, "unknown error"), e)
        }
    }
}

/**
 * @throws ApiError if error
 * @return if success
 */
suspend fun HttpClient.validateResponse(response: HttpResponse) {
    //TODO HYUN [multi-platform2] : consider how to set header

    if (response.status.isSuccess()) {
        return
    }

    if (response.status.isApiError()) {
        val json = Json(JsonConfiguration.Stable)
        throw ApiError(json.parse(ApiErrorBody.serializer(), response.readText()))
    }


    throw ApiError(
        ApiErrorBody(
            ApiErrorCode.UNKNOWN,
            "unknown error occurred : ${response.status}, Text : ${response.readText()}"
        )
    )
}