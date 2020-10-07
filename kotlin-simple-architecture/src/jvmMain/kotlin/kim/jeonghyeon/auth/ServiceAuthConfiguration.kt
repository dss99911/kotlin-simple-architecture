package kim.jeonghyeon.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.authentication
import io.ktor.auth.basic
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.auth.jwt.jwt
import io.ktor.sessions.*
import kim.jeonghyeon.db.User
import java.io.File


//todo currently, support only one authentication.
// consider to support multiple authentication
internal lateinit var selectedServiceAuthType: ServiceAuthType

class SessionServiceAuthConfiguration(var sessions: (Sessions.Configuration.() -> Unit)? = null) : ServiceAuthConfiguration(ServiceAuthType.SESSION) {
    override fun initialize(pipeline: Application): Unit = with(pipeline) {
        authentication {
            basic {
                validate {
                    null
                }

                skipWhen {
                    it.sessions.get(HEADER_NAME_TOKEN) != null
                }
            }
        }

        //todo support different session storage and expiration
        //todo consider sessionIdProvider, if it's short. it's weak on brute-force attack
        //todo consider block ip if session id is not correct several times.
        //todo session data always sent to client on cookies case. even not set session.
        // is there same issue on header?
        // https://youtrack.jetbrains.com/issue/KTOR-912
        install(Sessions) {
            header<UserSession>(
                HEADER_NAME_TOKEN,
                directorySessionStorage(File(".sessions"), cached = true)
            )

            @Suppress("UNCHECKED_CAST")
            sessionIdProvider = providers.first { it.name == HEADER_NAME_TOKEN } as SessionProvider<UserSession>

            sessions?.invoke(this)
        }
    }
}

lateinit var sessionIdProvider: SessionProvider<UserSession>

/**
 * this is not session based.
 * so, able to reduce server memory. also, auth server traffic will be reduced.
 * but, can't expires token manually(time based expiration can be configured by claim),
 * need additional implementation for expiring token manually.
 * this doesn't provide that additional implementation because there are several ways to implement.
 * and each service has different security requirements. so, implementation also different.
 *
 */
class JwtServiceAuthConfiguration(var algorithm: Algorithm? = null) : ServiceAuthConfiguration(ServiceAuthType.JWT) {
    override fun initialize(pipeline: Application):Unit = with(pipeline) {
        jwtAlgorithm = algorithm!!

        authentication {
            jwt {
                verifier(makeJwtVerifier())

                validate { credential -> JWTPrincipal(credential.payload) }
            }
        }
    }

    //todo need more claims?
    private fun makeJwtVerifier(): JWTVerifier = JWT.require(jwtAlgorithm).build()

    companion object {
        lateinit var jwtAlgorithm: Algorithm
    }
}

abstract class ServiceAuthConfiguration(internal val serviceAuthType: ServiceAuthType) {
    abstract fun initialize(pipeline: Application)

}