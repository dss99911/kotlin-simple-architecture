package kim.jeonghyeon.backend.user

import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.sessions.get
import kim.jeonghyeon.auth.AuthenticationType
import kim.jeonghyeon.backend.auth.SampleClaims
import kim.jeonghyeon.db.User
import kim.jeonghyeon.net.*
import kim.jeonghyeon.sample.api.SerializableUserDetail
import kim.jeonghyeon.sample.api.UserApi

class UserJwtController : UserApi {
    override suspend fun getUser(): SerializableUserDetail {
        return SerializableUserDetail(userId(), claim(SampleClaims.NAME).asString())
    }
}