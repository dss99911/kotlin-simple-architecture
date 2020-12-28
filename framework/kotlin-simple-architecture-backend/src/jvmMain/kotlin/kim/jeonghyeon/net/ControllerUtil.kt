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
import kim.jeonghyeon.net.ControllerUtil.authentication


object ControllerUtilArchitecture {
    suspend fun claim(name: String): Claim = (authentication().principal as JWTPrincipal).payload.getClaim(name)

    suspend fun userId(): Long = if (selectedServiceAuthType == ServiceAuthType.JWT) {
        claim(PublicClaims.JWT_ID).asString().toLong()
    } else {
        ControllerUtil.sessions().get<UserSession>()!!.userId
    }

    suspend fun userExtra(name: String): String? = if (selectedServiceAuthType == ServiceAuthType.JWT) {
        claim(name).asString()
    } else {
        ControllerUtil.sessions().get<UserSession>()!!.extra[name]
    }


}

