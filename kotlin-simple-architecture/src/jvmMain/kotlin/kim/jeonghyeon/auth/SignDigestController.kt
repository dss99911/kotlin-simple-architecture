package kim.jeonghyeon.auth

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.digest
import io.ktor.request.uri
import io.ktor.sessions.*
import io.ktor.util.hex
import kim.jeonghyeon.db.User
import kim.jeonghyeon.db.UserQueries
import kim.jeonghyeon.di.serviceLocator
import kim.jeonghyeon.net.addControllerBeforeInstallSimpleRouting
import java.io.File

open class SignDigestController(
    userQueries: UserQueries = serviceLocator.userQueries
) : SignController(userQueries), SignDigestApi {
    final override suspend fun signUpHashed(id: String, ha1: String, extra: String) {
        assertUserNotExists(id)
        userQueries.insert(id, ha1)
        signUp(id, extra)
    }

    override suspend fun getNonce() {

    }
}

class SignDigestConfiguration(var controller: SignDigestController? = null, var sessions: (Sessions.Configuration.() -> Unit)? = null) : SignAuthConfiguration(AuthenticationType.DIGEST) {
    override fun initialize(pipeline: Application): Unit = with(pipeline) {
        install(Authentication) {
            digest(AuthenticationType.DIGEST.name) {
                realm = REALM_SIMPLE_API
                digestProvider { userName, realm ->
                    validateUserDigest(userName)
                }
                skipWhen {
                    if (it.request.uri.isNonceApiUri() || it.request.uri.isSignInApiUri()) {
                        return@skipWhen false
                    }
                    it.sessions.get(HEADER_NAME_TOKEN) != null
                }
            }
        }

        install(Sessions) {
            header<User>(
                HEADER_NAME_TOKEN,
                directorySessionStorage(File(".sessions"), cached = true)
            )
            sessions?.invoke(this)
        }
    }

    override fun getController(): SignController = controller?: SignDigestController()
}

internal fun validateUserDigest(id: String): ByteArray? {
    val user = serviceLocator.userQueries.selectOne(id).executeAsOneOrNull() ?: return null
    return hex(user.password)
}

private fun String.isNonceApiUri(): Boolean {
    return this == "/" + SignDigestApi::class.qualifiedName!!.replace(
        ".",
        "/"
    ) + "/" + SignDigestApi::getNonce.name
}
private fun String.isSignInApiUri(): Boolean {
    return this == "/" + SignDigestApi::class.qualifiedName!!.replace(
        ".",
        "/"
    ) + "/" + "signIn"
}
