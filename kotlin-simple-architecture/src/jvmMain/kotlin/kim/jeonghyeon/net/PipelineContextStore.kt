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
import io.ktor.util.Attributes
import io.ktor.util.pipeline.PipelineContext
import kim.jeonghyeon.auth.*
import kim.jeonghyeon.auth.selectedServiceAuthType
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class PipelineContextStore(val context: PipelineContext<Unit, ApplicationCall>, var responded: Boolean = false) :
    CoroutineContext.Element {
    override val key: CoroutineContext.Key<*>
        get() = Key

    companion object Key : CoroutineContext.Key<PipelineContextStore>

}

suspend fun call(): ApplicationCall =
    coroutineContext[PipelineContextStore]!!.context.call

suspend fun headers(): Headers = call().request.headers
suspend fun setResponded() { coroutineContext[PipelineContextStore]!!.responded = true }

suspend fun sessions(): CurrentSession = call().sessions

suspend fun attributes(): Attributes = call().attributes

suspend fun authentication(): AuthenticationContext = call().authentication

suspend fun userId(): Long = if (selectedServiceAuthType == ServiceAuthType.JWT) {
    claim(PublicClaims.JWT_ID).asString().toLong()
} else {
    sessions().get<UserSession>()!!.userId
}

suspend fun userExtra(name: String): String? = if (selectedServiceAuthType == ServiceAuthType.JWT) {
    claim(name).asString()
} else {
    sessions().get<UserSession>()!!.extra[name]
}
suspend fun claim(name: String): Claim = (authentication().principal as JWTPrincipal).payload.getClaim(name)