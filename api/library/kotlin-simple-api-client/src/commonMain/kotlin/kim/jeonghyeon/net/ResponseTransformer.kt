package kim.jeonghyeon.net

import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.network.sockets.*
import kim.jeonghyeon.annotation.SimpleArchInternal
import kim.jeonghyeon.auth.HEADER_NAME_TOKEN
import kim.jeonghyeon.net.ResponseTransformerInternal.saveToken
import kim.jeonghyeon.net.error.*
import kim.jeonghyeon.pergist.KEY_USER_TOKEN
import kim.jeonghyeon.pergist.Preference
import kim.jeonghyeon.pergist.removeUserToken
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

interface ResponseTransformer {
    suspend fun <OUT> transform(response: HttpResponse, callInfo: ApiCallInfo, returnType: KType, returnTypeInfo: TypeInfo): OUT
    suspend fun <OUT> error(e: Throwable, callInfo: ApiCallInfo, returnType: KType, returnTypeInfo: TypeInfo): OUT
}

@SimpleArchInternal
object ResponseTransformerInternal {
    inline fun getDefaultResponseTransformer(): ResponseTransformer = object : ResponseTransformer {
        override suspend fun <OUT> transform(
            response: HttpResponse,
            callInfo: ApiCallInfo,
            returnType: KType,
            returnTypeInfo: TypeInfo
        ): OUT {
            if (callInfo.isAuthRequired) {
                response.saveToken()
            }

            validateResponse(response)
            if (returnType.classifier == Unit::class) {
                @Suppress("UNCHECKED_CAST")
                return Unit as OUT
            }
            @Suppress("UNCHECKED_CAST")
            return response.call.receive(returnTypeInfo) as OUT
        }

        override suspend fun <OUT> error(
            e: Throwable,
            callInfo: ApiCallInfo,
            returnType: KType,
            returnTypeInfo: TypeInfo
        ): OUT {
            throwException(e)
        }
    }
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
            is ClientRequestException -> {
                val status = e.response.status
                if (status == HttpStatusCode.Unauthorized) {
                    /**
                     * todo if it's unauthorized on response, remove token on preference.
                     *  when remove token, check api url and realm.
                     */
                    Preference().removeUserToken()
                }
                ApiErrorBody(status.value, status.description).toError(e)
            }
            else -> {
                ApiError(ApiErrorBody.Unknown, e)
            }
        }
    }

    fun HttpResponse.saveToken() {
        //if sigh out, tokenString will be ""
        val tokenString = headers[HEADER_NAME_TOKEN]?:return
        Preference().setEncryptedString(Preference.KEY_USER_TOKEN, tokenString)
    }

}



