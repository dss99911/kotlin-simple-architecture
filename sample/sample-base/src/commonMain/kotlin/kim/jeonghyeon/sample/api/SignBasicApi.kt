package kim.jeonghyeon.sample.api

import io.ktor.util.*
import io.ktor.util.encodeBase64
import io.ktor.utils.io.core.*
import kim.jeonghyeon.annotation.Api
import kim.jeonghyeon.annotation.Authenticate
import kim.jeonghyeon.annotation.Header

const val HEADER_AUTHORIZATION = "Authorization"

@Api
interface SignBasicApi : SignApi {
    override suspend fun signUp(id: String, password: String, extra: String)

    @Authenticate
    suspend fun signIn(@Header(HEADER_AUTHORIZATION)authorization: String)

    @Authenticate
    suspend fun signOut()

    override suspend fun signIn(id: String, password: String) {
        signIn(constructBasicAuthorizationHeader(id, password))
    }

    @OptIn(InternalAPI::class)
    private fun constructBasicAuthorizationHeader(username: String, password: String): String {
        val authString = "$username:$password"
        val authBuf = authString.toByteArray().encodeBase64()

        return "Basic $authBuf"
    }
}

interface SignApi {
    suspend fun signIn(id: String, password: String)

    /**
     * @param extra you can add any additional user information here. and override signUp on SignController
     */
    suspend fun signUp(id: String, password: String, extra: String)
}



