package kim.jeonghyeon.auth
import com.soywiz.krypto.SecureRandom
import com.soywiz.krypto.md5
import io.ktor.client.features.auth.*
import io.ktor.client.features.auth.providers.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.auth.*
import io.ktor.util.*
import io.ktor.utils.io.charsets.*
import io.ktor.utils.io.core.*
import kim.jeonghyeon.annotation.Api
import kim.jeonghyeon.annotation.Authenticate
import kim.jeonghyeon.annotation.Header
import kim.jeonghyeon.net.HttpResponseStore
import kim.jeonghyeon.net.error.ApiErrorBody
import kim.jeonghyeon.net.error.errorApi
import kim.jeonghyeon.net.error.isApiErrorOf
import kim.jeonghyeon.net.response
import kim.jeonghyeon.type.atomic
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

const val REALM_SIMPLE_API = "simpleRealm"
private const val HASH_ALGORITHM = "MD5"
const val AUTH_NAME_DIGEST = "DIGEST"
@Api
interface SignDigestApi : SignApi {

    suspend fun signUpHashed(signId: String, ha1: String, extra: String?)

    @Authenticate(AUTH_NAME_DIGEST)
    suspend fun getNonce()

    override suspend fun signUp(signId: String, password: String, extra: String?) {
        signUpHashed(signId, hex(makeDigest("$signId:$REALM_SIMPLE_API:$password")), extra)
    }

    @OptIn(InternalAPI::class)
    private suspend fun makeDigest(data: String): ByteArray {
        //todo when Ktor support digest on IOS, use ktor
//        val digest = Digest(HASH_ALGORITHM)
//        return digest.build(data.toByteArray(Charsets.UTF_8))
        return data.toByteArray(Charsets.UTF_8).md5().bytes
    }

    /**
     * if already signed in, throw ApiErrorBody(1001, "Already Signed In")
     * handle this exception and decide if signed out and retry or
     */
    override suspend fun signIn(signId: String, password: String) {
        withContext(coroutineContext + HttpResponseStore()) {
            try {
                getNonce()
            } catch (e: Exception) {
                if (e.isApiErrorOf(ApiErrorBody.Unauthorized)) {
                    val headerValue = response().headers[HttpHeaders.WWWAuthenticate]?: throw e
                    val httpAuthHeader = parseAuthorizationHeader(headerValue)?: throw e

                    val digestAuthProvider = DigestAuthProviderTemp(signId, password, REALM_SIMPLE_API, HASH_ALGORITHM)
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

    @Authenticate(AUTH_NAME_DIGEST)
    suspend fun signIn(@Header(HEADER_AUTHORIZATION)authorization: String)

    @Authenticate
    override suspend fun signOut()

}

/**
 * Todo : digest is not supported on IOS.
 *  when it's supported on ktor client, change to [DigestAuthProvider]
 */
class DigestAuthProviderTemp(
    val username: String,
    val password: String,
    val realm: String?,
    val algorithmName: String = "MD5"
) : AuthProvider {
    override val sendWithoutRequest: Boolean = false

    private val serverNonce = atomic<String?>(null)
    private val qop = atomic<String?>(null)
    private val opaque = atomic<String?>(null)
    private val secureRandom = SecureRandom()

    /**
     * todo [generateNonce] creating strange string on ios, if it's fixed use it
     */
    private val clientNonce = makeNonce()

    private val requestCounter = atomic(0)

    override fun isApplicable(auth: HttpAuthHeader): Boolean {
        if (auth !is HttpAuthHeader.Parameterized ||
            auth.parameter("realm") != realm ||
            auth.authScheme != AuthScheme.Digest
        ) return false

        val newNonce = auth.parameter("nonce") ?: return false
        val newQop = auth.parameter("qop")
        val newOpaque = auth.parameter("opaque")

        serverNonce.value = newNonce
        qop.value = newQop
        opaque.value = newOpaque

        return true
    }

    override suspend fun addRequestHeaders(request: HttpRequestBuilder) {
        val value = requestCounter.value
        requestCounter.value = value + 1
        val nonceCount = value + 1
        val methodName = request.method.value.toUpperCase()
        val url = URLBuilder().takeFrom(request.url).build()

        val nonce = serverNonce.value!!
        val serverOpaque = opaque.value
        val actualQop = qop.value

        val credential = makeDigest("$username:$realm:$password")

        val start = hex(credential)
        val end = hex(makeDigest("$methodName:${url.fullPath}"))
        val tokenSequence = if (actualQop == null) listOf(start, nonce, end) else listOf(start, nonce, nonceCount, clientNonce, actualQop, end)
        val token = makeDigest(tokenSequence.joinToString(":"))
        val auth = HttpAuthHeader.Parameterized(AuthScheme.Digest, mutableMapOf<String, String>().apply {
            realm?.let { this["realm"] = it }
            serverOpaque?.let { this["opaque"] = it }
            this["username"] = username
            this["nonce"] = nonce
            this["cnonce"] = clientNonce
            this["response"] = hex(token)
            this["uri"] = url.fullPath
            actualQop?.let { this["qop"] = it }
            this["nc"] = nonceCount.toString()
        })

        request.headers {
            append(HttpHeaders.Authorization, auth.render())
        }
    }

    private fun makeDigest(data: String): ByteArray {
//        val digest = Digest(algorithmName)
//        return digest.build(data.toByteArray(Charsets.UTF_8))
        return data.toByteArray(Charsets.UTF_8).md5().bytes
    }

    fun makeNonce(): String {
        val byteArray = ByteArray(32)
        return hex(secureRandom.nextBytes(byteArray))
    }
}