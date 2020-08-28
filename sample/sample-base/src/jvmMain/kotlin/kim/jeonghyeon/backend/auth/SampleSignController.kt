package kim.jeonghyeon.backend.auth

import kim.jeonghyeon.auth.*
import kim.jeonghyeon.backend.di.serviceLocator
import kim.jeonghyeon.db.User
import kim.jeonghyeon.extension.fromJsonString
import kim.jeonghyeon.extension.toJsonString
import kim.jeonghyeon.sample.api.SerializableUserDetail
import kim.jeonghyeon.util.log

/**
 * use one of [SampleSignBasicController], [SampleSignDigestController]
 * I added two controller for example of each
 */
class SampleSignBasicController :
    SignBasicController() {

    override suspend fun onUserCreated(user: User, extra: String?) {
        generateToken(user.signId)
    }

    override fun MutableMap<String, String>.makeServiceAuthExtraOnSignIn(user: User) {
        val userDetail = user.extra!!.fromJsonString<SerializableUserDetail>()
        put(USER_EXTRA_KEY_NAME, userDetail.name)
        put(USER_EXTRA_KEY_SIGN_ID, user.signId)
    }
}

class SampleSignDigestController :
    SignDigestController() {

    override suspend fun onUserCreated(user: User, extra: String?) {
        generateToken(user.signId)
    }

    override fun MutableMap<String, String>.makeServiceAuthExtraOnSignIn(user: User) {
        val userDetail = user.extra!!.fromJsonString<SerializableUserDetail>()
        put(USER_EXTRA_KEY_NAME, userDetail.name)
        put(USER_EXTRA_KEY_SIGN_ID, user.signId)
    }
}


class SampleSignOAuthController : SignOAuthController() {
    override suspend fun signUp(
        platform: ClientPlatform,
        oAuthServerName: OAuthServerName,
        redirectUrl: String,
        packageName: String?
    ) {
        signUpAndInAtSameTime(platform, oAuthServerName, redirectUrl, packageName)
    }

    override suspend fun fetchExtraFromOAuthServerOnSignUp(
        oAuthServerName: OAuthServerName,
        signId: String,
        accessToken: String,
        idMap: Map<String, String?>
    ): String? {
        log.i("user information : $idMap")
        return SerializableUserDetail(null, idMap["name"]?:"null").toJsonString()
    }

    override fun MutableMap<String, String>.makeServiceAuthExtraOnSignIn(user: User) {
        val userDetail = user.extra!!.fromJsonString<SerializableUserDetail>()
        put(USER_EXTRA_KEY_NAME, userDetail.name)
        put(USER_EXTRA_KEY_SIGN_ID, user.signId)
    }

}

const val USER_EXTRA_KEY_NAME = "name"
const val USER_EXTRA_KEY_SIGN_ID = "sign_id"