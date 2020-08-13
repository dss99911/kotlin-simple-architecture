package kim.jeonghyeon.sample.api

import io.ktor.util.*
import io.ktor.util.encodeBase64
import io.ktor.utils.io.core.*
import kim.jeonghyeon.annotation.Api
import kim.jeonghyeon.annotation.Authenticate
import kim.jeonghyeon.annotation.Header

const val HEADER_AUTHORIZATION = "Authorization"

@Api
interface SignApi {
    suspend fun signUp(id: String, name: String, password: String)

    @Authenticate
    suspend fun signIn(@Header(HEADER_AUTHORIZATION)authorization: String)

    @Authenticate
    suspend fun signOut()

}

@OptIn(InternalAPI::class)
internal fun constructBasicAuthorizationHeader(username: String, password: String): String {
    val authString = "$username:$password"
    val authBuf = authString.toByteArray().encodeBase64()

    return "Basic $authBuf"
}