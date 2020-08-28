package kim.jeonghyeon.auth

import io.ktor.http.*
import kim.jeonghyeon.net.SimpleApiUtil
import kim.jeonghyeon.pergist.Preference

class SignOAuthClient(private val serverUrl: String): SignOAuthApi {

    suspend fun signUp(oAuthServerName: OAuthServerName,
                       redirectUrl: String) {
        signUp(platform, oAuthServerName, redirectUrl, packageName)
    }

    suspend fun signIn(oAuthServerName: OAuthServerName,
                       redirectUrl: String) {
        signIn(platform, oAuthServerName, redirectUrl, packageName)
    }

    fun saveToken(url: Url) {
        val token = url.parameters[QUERY_TOKEN]?: error("token not exists")
        Preference().setEncryptedString(HEADER_NAME_TOKEN, token)
    }

    override suspend fun signUp(
        platform: ClientPlatform,
        oAuthServerName: OAuthServerName,
        redirectUrl: String,
        packageName: String?
    ) {
        sign(SIGN_UP_PATH, platform, oAuthServerName, redirectUrl, packageName)
    }

    override suspend fun signIn(
        platform: ClientPlatform,
        oAuthServerName: OAuthServerName,
        redirectUrl: String,
        packageName: String?
    ) {
        sign(SIGN_IN_PATH, platform, oAuthServerName, redirectUrl, packageName)
    }

    /**
     * sign on web browser
     *
     * if it's android device, there is two way, 1. request from app, 2. request from web browser.
     * user can sign from web browser, and installed app already signed in with different account.
     */
    private fun sign(apiName: String, platform: ClientPlatform, oauthServerName: OAuthServerName, redirectUrl: String, packageName: String?) = SimpleApiUtil.run {
        //javscription doesn't support reflection yet
        val mainPath = "kim/jeonghyeon/auth/SignOAuthApi"
        val queryParams = listOf(
            QUERY_SERVER_URL to convertParameter(serverUrl),//server doesn't know it's entry point url
            QUERY_OAUTH_SERVER_NAME to convertParameter(oauthServerName),
            QUERY_REDIRECT_URL to convertParameter(redirectUrl),
            QUERY_PLATFORM to convertParameter(platform),
            QUERY_PACKAGE_NAME to convertParameter(packageName)

        ).formUrlEncode()

        val url = "$serverUrl/$mainPath/$apiName?$queryParams"

        loadUrlInBrowser(url)
    }
}

expect val platform: ClientPlatform

expect fun loadUrlInBrowser(url: String)
expect val packageName: String?
