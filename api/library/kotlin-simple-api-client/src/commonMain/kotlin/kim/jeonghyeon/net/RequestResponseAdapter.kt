package kim.jeonghyeon.net

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.auth.*
import io.ktor.network.sockets.*
import kim.jeonghyeon.annotation.SimpleArchInternal
import kim.jeonghyeon.net.error.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

interface RequestResponseAdapter {
    suspend fun beforeBuildRequest(callInfo: ApiCallInfo, client: HttpClient): ApiCallInfo
    suspend fun buildRequest(builder: HttpRequestBuilder, callInfo: ApiCallInfo)
    suspend fun <OUT> transformResponse(response: HttpResponse, callInfo: ApiCallInfo, returnTypeInfo: TypeInfo): OUT
    suspend fun <OUT> handleException(e: Throwable, callInfo: ApiCallInfo, returnTypeInfo: TypeInfo): OUT
}

interface RequestResponseListener {
    suspend fun beforeBuildRequest(callInfo: ApiCallInfo, client: HttpClient): ApiCallInfo
    suspend fun buildRequest(builder: HttpRequestBuilder, callInfo: ApiCallInfo)
    suspend fun <OUT> transformResponse(response: HttpResponse, callInfo: ApiCallInfo, returnTypeInfo: TypeInfo)
    suspend fun <OUT> handleException(e: Throwable, callInfo: ApiCallInfo, returnTypeInfo: TypeInfo)
}

@OptIn(SimpleArchInternal::class)
inline fun getDefaultRequestResponseAdapter(listener: RequestResponseListener? = null): RequestResponseAdapter = object : RequestResponseAdapter {

    override suspend fun beforeBuildRequest(callInfo: ApiCallInfo, client: HttpClient): ApiCallInfo {
        return listener?.beforeBuildRequest(callInfo, client)?: callInfo
    }

    override suspend fun buildRequest(builder: HttpRequestBuilder, callInfo: ApiCallInfo) {
        listener?.buildRequest(builder, callInfo)
    }

    override suspend fun <OUT> transformResponse(
        response: HttpResponse,
        callInfo: ApiCallInfo,
        returnTypeInfo: TypeInfo
    ): OUT {
        listener?.transformResponse<OUT>(response, callInfo, returnTypeInfo)

        RequestResponseAdapterInternal.validateResponse(response)
        if (returnTypeInfo.kotlinType?.classifier == Unit::class) {
            @Suppress("UNCHECKED_CAST")
            return Unit as OUT
        }

        //todo "aa" is returned as "\"aa\" if use `response.call.receive(returnTypeInfo)`
        // I think it should be "aa", need to analyze the reason.
        if (returnTypeInfo.type == String::class) {
            val text = response.readText()
            return Json { }.decodeFromString(serializer(returnTypeInfo.kotlinType!!), text) as OUT
        }

        @Suppress("UNCHECKED_CAST")
        return response.call.receive(returnTypeInfo) as OUT
    }

    override suspend fun <OUT> handleException(
        e: Throwable,
        callInfo: ApiCallInfo,
        returnTypeInfo: TypeInfo
    ): OUT {
        listener?.handleException<OUT>(e, callInfo, returnTypeInfo)
        RequestResponseAdapterInternal.throwException(e)
    }
}

@SimpleArchInternal
object RequestResponseAdapterInternal {

    /**
     * @throws ApiError if error
     * @return if success
     */
    suspend fun validateResponse(response: HttpResponse) {
        if (response.status.isApiError()) {
            errorApi(response.receive<ApiErrorBody>())
        }

        if (response.status.isDeeplinkError()) {
            errorDeeplink(response.receive())
        }

        if (response.status.isSuccess()) {
            return
        }

        errorApi(ApiErrorBody.CODE_UNKNOWN, "unknown error occurred : ${response.status}, Text : ${response.readText()}")
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
            else -> {
                ApiError(ApiErrorBody.Unknown, e)
            }
        }
    }

}



