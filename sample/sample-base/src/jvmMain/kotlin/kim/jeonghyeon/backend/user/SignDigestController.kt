package kim.jeonghyeon.backend.user

import io.ktor.util.hex
import kim.jeonghyeon.backend.di.serviceLocator
import kim.jeonghyeon.const.idAlreadyExists
import kim.jeonghyeon.net.error.ApiErrorBody
import kim.jeonghyeon.net.error.errorApi
import kim.jeonghyeon.sample.UserQueries
import kim.jeonghyeon.sample.api.SignDigestApi

class SignDigestController(userQueries: UserQueries = serviceLocator.userQueries) : SignController(userQueries), SignDigestApi {
    override suspend fun signUpHashed(id: String, ha1: String, name: String) {
        val exists = userQueries.selectOne(id).executeAsOneOrNull() != null
        if (exists) {
            errorApi(ApiErrorBody.idAlreadyExists)
        }
        userQueries.insert(id, name, ha1)
        addUserToSession(id)
    }

    override suspend fun getNonce() {

    }
}

fun validateUserDigest(id: String): ByteArray? {
    val user = serviceLocator.userQueries.selectOne(id).executeAsOneOrNull()?: return null
    return hex(user.password)
}

fun String.isNonceApiUri(): Boolean {
    return this == "/" + SignDigestApi::class.qualifiedName!!.replace(".", "/") + "/" + SignDigestApi::getNonce.name
}