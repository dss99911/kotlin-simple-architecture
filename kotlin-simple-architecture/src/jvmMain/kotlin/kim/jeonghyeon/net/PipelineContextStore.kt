package kim.jeonghyeon.net

import com.auth0.jwt.impl.PublicClaims
import com.auth0.jwt.interfaces.Claim
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.AuthenticationContext
import io.ktor.auth.authentication
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.http.Headers
import io.ktor.sessions.CurrentSession
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import io.ktor.util.pipeline.PipelineContext
import kim.jeonghyeon.auth.AuthenticationType
import kim.jeonghyeon.auth.authType
import kim.jeonghyeon.db.User
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class PipelineContextStore(val context: PipelineContext<Unit, ApplicationCall>) :
    CoroutineContext.Element {
    override val key: CoroutineContext.Key<*>
        get() = Key

    companion object Key : CoroutineContext.Key<PipelineContextStore>

}

suspend fun call(): ApplicationCall =
    coroutineContext[PipelineContextStore]!!.context.call

suspend fun headers(): Headers = call().request.headers

suspend fun sessions(): CurrentSession = call().sessions

suspend fun authentication(): AuthenticationContext = call().authentication

suspend fun userId(): String = if (authType == AuthenticationType.JWT) {
    claim(PublicClaims.JWT_ID).asString()
} else {
    sessions().get<User>()!!.id
}

suspend fun claim(name: String): Claim = (authentication().principal as JWTPrincipal).payload.getClaim(name)