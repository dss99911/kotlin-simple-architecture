package kim.jeonghyeon.backend.auth

import com.auth0.jwt.JWTCreator
import kim.jeonghyeon.auth.SignJwtController
import kim.jeonghyeon.backend.di.serviceLocator
import kim.jeonghyeon.extension.fromJsonString
import kim.jeonghyeon.sample.UserDetailQueries
import kim.jeonghyeon.sample.api.SerializableUserDetail


class SampleSignJwtController(val userDetailQueries: UserDetailQueries = serviceLocator.userDetailQueries) :
    SignJwtController() {

    override suspend fun signUp(id: String, extra: String) {
        val userDetail = extra.fromJsonString<SerializableUserDetail>()
        userDetailQueries.insert(id, userDetail.name)
    }

    override fun JWTCreator.Builder.onJwtTokenBuild(userId: String) {
        val userDetail = userDetailQueries.selectOne(userId).executeAsOne()
        withClaim(SampleClaims.NAME, userDetail.name)
    }
}

object SampleClaims {
    const val NAME = "name"
}