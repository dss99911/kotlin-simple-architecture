package kim.jeonghyeon.sample.api

import io.ktor.client.features.auth.providers.DigestAuthProvider
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.auth.parseAuthorizationHeader
import io.ktor.util.*
import io.ktor.util.encodeBase64
import io.ktor.utils.io.charsets.Charsets
import io.ktor.utils.io.core.*
import kim.jeonghyeon.annotation.Api
import kim.jeonghyeon.annotation.Authenticate
import kim.jeonghyeon.annotation.Header
import kim.jeonghyeon.net.HttpResponseStore
import kim.jeonghyeon.net.error.ApiError
import kim.jeonghyeon.net.error.ApiErrorBody
import kim.jeonghyeon.net.error.errorApi
import kim.jeonghyeon.net.error.isApiErrorOf
import kim.jeonghyeon.net.response
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

const val AUTHENTICATE_NAME_DIGEST = "digest"
const val REALM_SIMPLE_API = "simpleRealm"
private const val HASH_ALGORITHM = "MD5"

@Api
interface SignDigestApi : SignApi {

    suspend fun signUpHashed(id: String, ha1: String, extra: String)

    @Authenticate(AUTHENTICATE_NAME_DIGEST)
    suspend fun getNonce()

    override suspend fun signUp(id: String, password: String, extra: String) {
        signUpHashed(id, hex(makeDigest("$id:$REALM_SIMPLE_API:$password")), extra)
    }

    @OptIn(InternalAPI::class)
    suspend fun makeDigest(data: String): ByteArray {
        val digest = Digest(HASH_ALGORITHM)
        return digest.build(data.toByteArray(Charsets.UTF_8))
    }

    /**
     * if already signed in, throw ApiErrorBody(1001, "Already Signed In")
     * handle this exception and decide if signed out and retry or
     */
    override suspend fun signIn(id: String, password: String) {
        withContext(coroutineContext + HttpResponseStore()) {
            try {
                getNonce()
            } catch (e: Exception) {
                if (e.isApiErrorOf(ApiErrorBody.Unauthorized)) {
                    val headerValue = response().headers[HttpHeaders.WWWAuthenticate]?: throw e
                    val httpAuthHeader = parseAuthorizationHeader(headerValue)?: throw e

                    val digestAuthProvider = DigestAuthProvider(id, password, REALM_SIMPLE_API, HASH_ALGORITHM)
                    digestAuthProvider.isApplicable(httpAuthHeader)
                    val request = HttpRequestBuilder().apply {
                        method = HttpMethod.Post
                    }
                    digestAuthProvider.addRequestHeaders(request)

                    signIn(request.headers[HEADER_AUTHORIZATION]!!)

                    return@withContext
                } else {
                    throw e
                }
            }
            errorApi(ApiErrorBody.CODE_UNKNOWN)
        }
    }

    @Authenticate(AUTHENTICATE_NAME_DIGEST)
    suspend fun signIn(@Header(HEADER_AUTHORIZATION)authorization: String)

    @Authenticate(AUTHENTICATE_NAME_DIGEST)
    suspend fun signOut()

}