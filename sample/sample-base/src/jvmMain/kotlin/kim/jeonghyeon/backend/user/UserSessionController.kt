package kim.jeonghyeon.backend.user

import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.sessions.get
import kim.jeonghyeon.auth.AuthenticationType
import kim.jeonghyeon.backend.auth.SampleClaims
import kim.jeonghyeon.backend.di.serviceLocator
import kim.jeonghyeon.db.User
import kim.jeonghyeon.net.*
import kim.jeonghyeon.sample.UserDetailQueries
import kim.jeonghyeon.sample.api.SerializableUserDetail
import kim.jeonghyeon.sample.api.UserApi
import kim.jeonghyeon.sample.api.serializable

class UserSessionController(val userDetailQueries: UserDetailQueries = serviceLocator.userDetailQueries) : UserApi {
    override suspend fun getUser(): SerializableUserDetail {
        //todo use inmemory cache to fetch user detail.
        return userDetailQueries.selectOne(userId()).executeAsOne().serializable()
    }
}