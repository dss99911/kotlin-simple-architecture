package kim.jeonghyeon.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTCreator
import io.ktor.application.Application
import io.ktor.auth.UserIdPrincipal
import io.ktor.response.header
import kim.jeonghyeon.db.User
import kim.jeonghyeon.db.UserQueries
import kim.jeonghyeon.di.log
import kim.jeonghyeon.net.authentication
import kim.jeonghyeon.net.call
import kim.jeonghyeon.net.error.ApiError
import kim.jeonghyeon.net.error.ApiErrorBody
import kim.jeonghyeon.net.error.errorApi
import kim.jeonghyeon.net.response
import kim.jeonghyeon.net.sessions

//todo currently, support only one authentication.
// consider to support multiple authentication
internal lateinit var authType: AuthenticationType

fun AuthenticationType.getCheckingAuthTypes(): List<AuthenticationType> {
    return when (this) {
        AuthenticationType.BASIC -> listOf(AuthenticationType.BASIC)
        AuthenticationType.DIGEST -> listOf(AuthenticationType.DIGEST)
        AuthenticationType.JWT -> listOf(AuthenticationType.DIGEST, AuthenticationType.JWT)
    }
}

abstract class SignController(val userQueries: UserQueries) {
    open suspend fun signOut() {
        sessions().clear(HEADER_NAME_TOKEN)
    }

    open suspend fun signUp(id: String, extra: String) {}

    open suspend fun signIn(authorization: String) {
        val id = authentication().principal<UserIdPrincipal>()?.name?: throw ApiError(ApiErrorBody.credentialInvalid)

        val user = userQueries.selectOne(id).executeAsOneOrNull() ?: throw ApiError(ApiErrorBody.credentialInvalid)

        putTokenToResponse(user)
    }

    open suspend fun putTokenToResponse(user: User) {
        sessions().set(HEADER_NAME_TOKEN, user)
    }

    protected fun assertUserNotExists(id: String) {
        val exists = userQueries.selectOne(id).executeAsOneOrNull() != null
        if (exists) {
            errorApi(ApiErrorBody.idAlreadyExists)
        }
    }
}