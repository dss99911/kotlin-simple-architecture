package kim.jeonghyeon.net

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.auth.*
import kim.jeonghyeon.annotation.SimpleArchInternal
import kim.jeonghyeon.auth.HEADER_NAME_TOKEN
import kim.jeonghyeon.net.SignRequestResponseAdapterInternal.putTokenHeader
import kim.jeonghyeon.net.SignRequestResponseAdapterInternal.saveToken
import kim.jeonghyeon.net.error.ApiErrorBody
import kim.jeonghyeon.pergist.KEY_USER_TOKEN
import kim.jeonghyeon.pergist.Preference
import kim.jeonghyeon.pergist.getUserToken
import kim.jeonghyeon.pergist.removeUserToken

@OptIn(SimpleArchInternal::class)
inline fun getDefaultRequestResponseAdapterForArchitecture(listener: RequestResponseListener? = null): RequestResponseAdapter = getDefaultRequestResponseAdapter(
        object : RequestResponseListener {
            override suspend fun beforeBuildRequest(callInfo: ApiCallInfo, client: HttpClient): ApiCallInfo {
                if (isApiBinding()) {
                    throw ApiBindingException(callInfo, client)
                }
                return callInfo
            }
            override suspend fun buildRequest(builder: HttpRequestBuilder, callInfo: ApiCallInfo) {
                if (callInfo.isAuthRequired) {
                    builder.putTokenHeader()
                }
                listener?.buildRequest(builder, callInfo)
            }

            override suspend fun <OUT> transformResponse(
                response: HttpResponse,
                callInfo: ApiCallInfo,
                returnTypeInfo: TypeInfo
            ) {
                listener?.transformResponse<OUT>(response, callInfo, returnTypeInfo)

                if (callInfo.isAuthRequired) {
                    response.saveToken()
                }
            }

            override suspend fun <OUT> handleException(
                e: Throwable,
                callInfo: ApiCallInfo,
                returnTypeInfo: TypeInfo
            ) {
                listener?.handleException<OUT>(e, callInfo, returnTypeInfo)

                if (e is ClientRequestException) {
                    val status = e.response.status
                    if (status == HttpStatusCode.Unauthorized) {
                        /**
                         * todo if it's unauthorized on response, remove token on preference.
                         *  when remove token, check api url and realm.
                         */
                        Preference().removeUserToken()
                    }
                    throw ApiErrorBody(status.value, status.description).toError(e)
                }
            }
        })

    @SimpleArchInternal
    object SignRequestResponseAdapterInternal {
        fun HttpRequestBuilder.putTokenHeader() {
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