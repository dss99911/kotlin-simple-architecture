package kim.jeonghyeon.backend.user

import io.ktor.auth.Principal
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.UserPasswordCredential
import io.ktor.sessions.clear
import io.ktor.sessions.set
import io.ktor.util.Digest
import io.ktor.util.InternalAPI
import io.ktor.util.build
import io.ktor.util.hex
import io.ktor.utils.io.core.toByteArray
import kim.jeonghyeon.backend.di.serviceLocator
import kim.jeonghyeon.backend.net.authentication
import kim.jeonghyeon.backend.net.sessions
import kim.jeonghyeon.const.credentialInvalid
import kim.jeonghyeon.const.idAlreadyExists
import kim.jeonghyeon.net.error.ApiError
import kim.jeonghyeon.net.error.ApiErrorBody
import kim.jeonghyeon.net.error.errorApi
import kim.jeonghyeon.sample.User
import kim.jeonghyeon.sample.UserQueries
import kim.jeonghyeon.sample.api.SignBasicApi
import java.security.MessageDigest

abstract class SignController(val userQueries: UserQueries) {
    open suspend fun signOut() {
        sessions().clear<User>()
    }

    protected suspend fun addUserToSession(id: String) {
        signOut()
        val user = userQueries.selectOne(id).executeAsOneOrNull() ?: throw ApiError(ApiErrorBody.credentialInvalid)
        sessions().set(user)
    }

    open suspend fun signIn(authorization: String) {
        val id = authentication().principal<UserIdPrincipal>()?.name?: throw ApiError(ApiErrorBody.credentialInvalid)
        addUserToSession(id)
    }
}

class SignBasicController(userQueries: UserQueries = serviceLocator.userQueries) : SignController(userQueries), SignBasicApi {
    override suspend fun signUp(id: String, password: String, name: String) {
        val digestedPassword = digest(password)

        val exists = userQueries.selectOne(id).executeAsOneOrNull() != null
        if (exists) {
            errorApi(ApiErrorBody.idAlreadyExists)
        }
        userQueries.insert(id, name, digestedPassword)
        addUserToSession(id)
    }
}

suspend fun validateUser(credentials: UserPasswordCredential): Principal? {
    val user = serviceLocator.userQueries.selectOne(credentials.name).executeAsOneOrNull()?: return null
    return if (user.password == digest(credentials.password)) {
        UserIdPrincipal(user.id)
    } else {
        null
    }
}


@OptIn(InternalAPI::class)
suspend fun digest(text: String): String {
    val digest = Digest("SHA-256")
    return hex(digest.build(text.toByteArray(Charsets.UTF_8)))
}