package kim.jeonghyeon.net

import com.auth0.jwt.impl.PublicClaims
import com.auth0.jwt.interfaces.Claim
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.*
import io.ktor.sessions.*
import io.ktor.util.*
import kim.jeonghyeon.auth.ServiceAuthType
import kim.jeonghyeon.auth.UserSession
import kim.jeonghyeon.auth.selectedServiceAuthType
import kotlin.coroutines.coroutineContext

object ControllerUtil {
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
}