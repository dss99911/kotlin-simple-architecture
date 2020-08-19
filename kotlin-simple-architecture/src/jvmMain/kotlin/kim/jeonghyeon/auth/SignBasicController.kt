package kim.jeonghyeon.auth

import io.ktor.application.Application
import io.ktor.application.ApplicationFeature
import io.ktor.application.install
import io.ktor.auth.*
import io.ktor.request.uri
import io.ktor.sessions.*
import io.ktor.util.*
import io.ktor.utils.io.core.toByteArray
import kim.jeonghyeon.db.User
import kim.jeonghyeon.db.UserQueries
import kim.jeonghyeon.di.serviceLocator
import java.io.File


open class SignBasicController(
    userQueries: UserQueries = serviceLocator.userQueries
) : SignController(userQueries), SignBasicApi {
    /**
     * override and add additional data on extra.
     */
    final override suspend fun signUp(id: String, password: String, extra: String) {
        val digestedPassword = digest(password)

        assertUserNotExists(id)

        userQueries.insert(id, digestedPassword)
        signUp(id, extra)
    }
}

class SignBasicConfiguration(var controller: SignBasicController? = null, var sessions: (Sessions.Configuration.() -> Unit)? = null) : SignAuthConfiguration(AuthenticationType.BASIC) {

    override fun initialize(pipeline: Application): Unit = with(pipeline) {
        install(Authentication) {
            basic(AuthenticationType.BASIC.name) {
                validate { credentials ->
                    validateUser(credentials)
                }
                skipWhen {
                    if (it.request.uri.isSignInApiUri()) {
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

    override fun getController(): SignController = controller?: SignBasicController()
}

private suspend fun validateUser(credentials: UserPasswordCredential): Principal? {
    val user = serviceLocator.userQueries.selectOne(credentials.name).executeAsOneOrNull() ?: return null
    return if (user.password == digest(credentials.password)) {
        UserIdPrincipal(user.id)
    } else {
        null
    }
}

@OptIn(InternalAPI::class)
private suspend fun digest(text: String): String {
    val digest = Digest("SHA-256")
    return hex(digest.build(text.toByteArray(Charsets.UTF_8)))
}

private fun String.isSignInApiUri(): Boolean {
    return this == "/" + SignBasicApi::class.qualifiedName!!.replace(
        ".",
        "/"
    ) + "/" + "signIn"
}
