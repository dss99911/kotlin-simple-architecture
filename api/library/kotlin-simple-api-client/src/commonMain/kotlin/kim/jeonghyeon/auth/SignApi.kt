package kim.jeonghyeon.auth

import io.ktor.client.*
import kim.jeonghyeon.annotation.Get
import kotlinsimpleapiclient.generated.net.createSimple


const val HEADER_NAME_TOKEN = "simple-user-token"

@Suppress("RUNTIME_ANNOTATION_NOT_SUPPORTED")
interface SignApi {

    @Get
    suspend fun signIn(signId: String, password: String)

    /**
     * @param extra you can add any additional user information here. and override signUp on SignController
     */
    @Get
    suspend fun signUp(signId: String, password: String, extra: String? = null)

    suspend fun signOut()
}

fun HttpClient.createSignApi(baseUrl: String, authType: SignInAuthType): SignApi =
    when (authType) {
        SignInAuthType.BASIC -> createSimple<SignBasicApi>(baseUrl)
        SignInAuthType.DIGEST -> createSimple<SignDigestApi>(baseUrl)
        SignInAuthType.OAUTH -> error("use ${SignOAuthClient::class.simpleName}")
    }

/**
 * authenticate on sign in
 */
enum class SignInAuthType(val authName: String) {
    BASIC(AUTH_NAME_BASIC), DIGEST(AUTH_NAME_DIGEST), OAUTH(AUTH_NAME_OAUTH)
}

/**
 * authenticate if valid token or session exists
 *
 * doesn't support multiple of this type
 * this is for each service apis,
 */
enum class ServiceAuthType {
    SESSION, JWT
}