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
        var adapter: RequestResponseAdapter? = null
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

        val NoConfig = Config()
    }
}

fun HttpClientConfig<*>.simpleApiCustom(block: SimpleApiCustom.Config.() -> Unit) {
    install(SimpleApiCustom, block)
}


abstract class RequestResponseAdapter {
    /**
     * before building request
     */
    open suspend fun beforeBuildRequest(callInfo: ApiCallInfo, client: HttpClient): ApiCallInfo = callInfo

    /**
     * after building request, all data is set on [builder].
     * so, if you need to change data, or retrieve data. utilize this function
     */
    open suspend fun buildRequest(builder: HttpRequestBuilder, callInfo: ApiCallInfo) {

    }

    /**
     * if server response and return type is different, use this.
     */
    abstract suspend fun <OUT> transformResponse(response: HttpResponse, callInfo: ApiCallInfo, returnTypeInfo: TypeInfo): OUT

    /**
     * handle exception.
     * if it's no error then return data.
     * if it's error throw exception
     */
    open suspend fun <OUT> handleException(e: Throwable, callInfo: ApiCallInfo, returnTypeInfo: TypeInfo): OUT {
        throw e
    }
}

abstract class RequestResponseListener {
    open suspend fun beforeBuildRequest(callInfo: ApiCallInfo, client: HttpClient): ApiCallInfo = callInfo
    open suspend fun buildRequest(builder: HttpRequestBuilder, callInfo: ApiCallInfo) {

    }

    open suspend fun <OUT> transformResponse(
        response: HttpResponse,
        callInfo: ApiCallInfo,
        returnTypeInfo: TypeInfo
    ) {

    }

    open suspend fun <OUT> handleException(e: Throwable, callInfo: ApiCallInfo, returnTypeInfo: TypeInfo) {

    }
}

@OptIn(SimpleArchInternal::class, ExperimentalStdlibApi::class)
fun SimpleApiCustom.Config.getApiAdapter(vararg listener: RequestResponseListener): RequestResponseAdapter = object : RequestResponseAdapter() {

    override suspend fun beforeBuildRequest(
        callInfo: ApiCallInfo,
        client: HttpClient
    ): ApiCallInfo {
        return listener.fold(callInfo, { acc, l ->
            l.beforeBuildRequest(acc, client)
        })
    }

    override suspend fun buildRequest(builder: HttpRequestBuilder, callInfo: ApiCallInfo) {
        listener.forEach {
            it.buildRequest(builder, callInfo)
        }
    }

    override suspend fun <OUT> transformResponse(
        response: HttpResponse,
        callInfo: ApiCallInfo,
        returnTypeInfo: TypeInfo
    ): OUT {
        listener.forEach {
            it.transformResponse<OUT>(response, callInfo, returnTypeInfo)
        }

        validateResponse(response)
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
        listener.forEach {
            it.handleException<OUT>(e, callInfo, returnTypeInfo)
        }

        throwException(e)
    }
}




/**
 * @throws ApiError if error
 * @return if success
 */
private suspend fun validateResponse(response: HttpResponse) {
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

private fun throwException(e: Throwable): Nothing {
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
