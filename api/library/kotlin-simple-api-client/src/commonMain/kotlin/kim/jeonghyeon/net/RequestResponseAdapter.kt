package kim.jeonghyeon.net

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.network.sockets.*
import io.ktor.util.*
import kim.jeonghyeon.annotation.SimpleArchInternal
import kim.jeonghyeon.net.error.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer


class SimpleApiCustom internal constructor(val config: Config) {

    class Config {
        val adapter: RequestResponseAdapter? = null
    }

    companion object Feature : HttpClientFeature<Config, SimpleApiCustom> {
        override val key: AttributeKey<SimpleApiCustom> = AttributeKey("SimpleApiCustom")

        override fun prepare(block: Config.() -> Unit): SimpleApiCustom =
            SimpleApiCustom(Config().apply(block))

        override fun install(feature: SimpleApiCustom, scope: HttpClient) {

        }

        fun HttpClient.getAdapter(): RequestResponseAdapter? {
            return feature(SimpleApiCustom)?.config?.adapter
        }
    }
}

fun HttpClientConfig<*>.simpleApiCustom(block: SimpleApiCustom.Config.() -> Unit) {
    install(SimpleApiCustom, block)
}


interface RequestResponseAdapter {
    suspend fun beforeBuildRequest(callInfo: ApiCallInfo, client: HttpClient): ApiCallInfo
    suspend fun buildRequest(builder: HttpRequestBuilder, callInfo: ApiCallInfo)
    suspend fun <OUT> transformResponse(response: HttpResponse, callInfo: ApiCallInfo, returnTypeInfo: TypeInfo): OUT
    suspend fun <OUT> handleException(e: Throwable, callInfo: ApiCallInfo, returnTypeInfo: TypeInfo): OUT
}

interface RequestResponseListener {
    suspend fun beforeBuildRequest(callInfo: ApiCallInfo, client: HttpClient): ApiCallInfo
    suspend fun buildRequest(builder: HttpRequestBuilder, callInfo: ApiCallInfo)
    suspend fun <OUT> transformResponse(
        response: HttpResponse,
        callInfo: ApiCallInfo,
        returnTypeInfo: TypeInfo
    )

    suspend fun <OUT> handleException(e: Throwable, callInfo: ApiCallInfo, returnTypeInfo: TypeInfo)
}

@OptIn(SimpleArchInternal::class, ExperimentalStdlibApi::class)
fun getDefaultRequestResponseAdapter(listener: RequestResponseListener? = null): RequestResponseAdapter =
    object : RequestResponseAdapter {

        override suspend fun beforeBuildRequest(
            callInfo: ApiCallInfo,
            client: HttpClient
        ): ApiCallInfo {
            return listener?.beforeBuildRequest(callInfo, client) ?: callInfo
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
            @Suppress("UNCHECKED_CAST")
            return Json { }.decodeFromString(serializer(returnTypeInfo.kotlinType!!), text) as OUT
        }

        if (returnTypeInfo.kotlinType!!.isMarkedNullable) {
            return try {
                @Suppress("UNCHECKED_CAST")
                response.call.receive(returnTypeInfo) as OUT
            } catch (e: NullPointerException) {
                //TODO as ktor doesn't support nullable for response. try catch
                // This is not proper way to handle
                // Using Wrapper is not good, as we have to use external api as well.
                @Suppress("UNCHECKED_CAST")
                null as OUT
            }
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

        errorApi(
            ApiErrorBody.CODE_UNKNOWN,
            "unknown error occurred : ${response.status}, Text : ${response.readText()}"
        )
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



