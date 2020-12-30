package kim.jeonghyeon.backend.auth

import kim.jeonghyeon.auth.*
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
        //if call this, when user is created, automatically signed-in as well.
        //if doesn't call this, user have to sign-in after sign-up
        generateToken(user.signId)
    }

    /**
     * set name, and sign id on token(in case of jwt token), or in-memory(in case of session)
     * in order not to load from db whenever service api is called
     */
    override fun MutableMap<String, String>.makeServiceAuthExtraOnSignIn(user: User) {
        //client send user's name by extra
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
        //idMap is from each oauth provider.
        //so, key is defined by each provider
        //google, and facebook has 'name' key for user's name.
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