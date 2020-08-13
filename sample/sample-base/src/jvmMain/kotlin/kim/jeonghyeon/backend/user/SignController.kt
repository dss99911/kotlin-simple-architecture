package kim.jeonghyeon.backend.user

import io.ktor.auth.Principal
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.UserPasswordCredential
import io.ktor.sessions.clear
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import io.ktor.sessions.set
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
import kim.jeonghyeon.sample.api.SignApi
import java.security.MessageDigest

class SignController(val userQueries: UserQueries = serviceLocator.userQueries) : SignApi {
    override suspend fun signUp(id: String, name: String, password: String) {
        val digestedPassword = digest(password)

        val exists = userQueries.selectOne(id).executeAsOneOrNull() != null
        if (exists) {
            errorApi(ApiErrorBody.idAlreadyExists)
        }
        userQueries.insert(id, name, digestedPassword)
        addUserToSession(id)
    }

    override suspend fun signIn(authorization: String) {
        val id = authentication().principal<UserIdPrincipal>()?.name?: throw ApiError(ApiErrorBody.credentialInvalid)
        addUserToSession(id)
    }

    override suspend fun signOut() {
        sessions().clear<User>()
    }

    private suspend fun addUserToSession(id: String) {
        signOut()
        val user = userQueries.selectOne(id).executeAsOneOrNull() ?: throw ApiError(ApiErrorBody.credentialInvalid)
        sessions().set(user)
    }
}

fun validateUser(credentials: UserPasswordCredential): Principal? {
    val user = serviceLocator.userQueries.selectOne(credentials.name).executeAsOneOrNull()?: return null
    return if (user.password == digest(credentials.password)) {
        UserIdPrincipal(user.id)
    } else {
        null
    }
}


fun digest(text: String): String {
    val md = MessageDigest.getInstance("SHA-256")
    return String(md.digest(text.toByteArray()))
}