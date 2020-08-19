package kim.jeonghyeon.auth

import io.ktor.client.HttpClient
import kim.jeonghyeon.kotlinsimplearchitecture.generated.net.createSimple


const val HEADER_NAME_TOKEN = "simple-user-token"

interface SignApi {
    suspend fun signIn(id: String, password: String)

    /**
     * @param extra you can add any additional user information here. and override signUp on SignController
     */
    suspend fun signUp(id: String, password: String, extra: String)

    suspend fun signOut()
}

fun HttpClient.createSignApi(baseUrl: String, authType: AuthenticationType): SignApi =
    when (authType) {
        AuthenticationType.BASIC -> createSimple<SignBasicApi>(baseUrl)
        AuthenticationType.DIGEST -> createSimple<SignDigestApi>(baseUrl)
        AuthenticationType.JWT -> createSimple<SignDigestApi>(baseUrl)
    }

enum class AuthenticationType {
    BASIC, DIGEST, JWT
}

