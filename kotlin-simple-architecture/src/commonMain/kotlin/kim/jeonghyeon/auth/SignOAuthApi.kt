package kim.jeonghyeon.auth

import kim.jeonghyeon.annotation.Api
import kim.jeonghyeon.annotation.Authenticate
import kim.jeonghyeon.annotation.Get
import kim.jeonghyeon.annotation.Query
import kotlinx.serialization.Serializable

const val SIGN_IN_PATH = "signIn"
const val SIGN_UP_PATH = "signUp"
const val QUERY_PLATFORM = "platform"
const val QUERY_OAUTH_SERVER_NAME = "oAuthServer"
const val QUERY_REDIRECT_URL = "redirectUrl"
const val QUERY_PACKAGE_NAME = "packageName"
const val QUERY_SERVER_URL = "serverUrl"
const val QUERY_TOKEN = "token"

const val AUTH_NAME_OAUTH = "OAUTH"

@Api
interface SignOAuthApi {

    @Authenticate(AUTH_NAME_OAUTH)
    @Get(SIGN_UP_PATH)
    suspend fun signUp(
        @Query(QUERY_PLATFORM) platform: ClientPlatform,
        @Query(QUERY_OAUTH_SERVER_NAME) oAuthServerName: OAuthServerName,
        @Query(QUERY_REDIRECT_URL) redirectUrl: String,
        @Query(QUERY_PACKAGE_NAME) packageName: String?
    )

    @Authenticate(AUTH_NAME_OAUTH)
    @Get(SIGN_IN_PATH)
    suspend fun signIn(
        @Query(QUERY_PLATFORM) platform: ClientPlatform,
        @Query(QUERY_OAUTH_SERVER_NAME) oAuthServerName: OAuthServerName,
        @Query(QUERY_REDIRECT_URL) redirectUrl: String,
        @Query(QUERY_PACKAGE_NAME) packageName: String?
    )
}


@Serializable
enum class ClientPlatform {
    ANDROID,
    IOS,
    JS
}

@Serializable
data class OAuthServerName(val name: String) {

    companion object {
        val GOOGLE: OAuthServerName = OAuthServerName("GOOGLE")
        val FACEBOOK: OAuthServerName = OAuthServerName("FACEBOOK")
    }
}

