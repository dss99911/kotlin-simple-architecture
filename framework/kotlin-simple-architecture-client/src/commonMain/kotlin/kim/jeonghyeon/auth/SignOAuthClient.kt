@file:Suppress("EXPERIMENTAL_API_USAGE")

package kim.jeonghyeon.auth

import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.http.*
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

    @Throws(Exception::class)
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
    private fun sign(apiName: String, platform: ClientPlatform, oauthServerName: OAuthServerName, redirectUrl: String, packageName: String?) {
        //javscription doesn't support reflection yet
        val mainPath = "kim/jeonghyeon/auth/SignOAuthApi"
        val queryParams = listOf(
            QUERY_SERVER_URL to serverUrl.toParameterString(),//server doesn't know it's entry point url
            QUERY_OAUTH_SERVER_NAME to oauthServerName.toParameterString(),
            QUERY_REDIRECT_URL to redirectUrl.toParameterString(),
            QUERY_PLATFORM to platform.toParameterString(),
            QUERY_PACKAGE_NAME to packageName.toParameterString()
        ).formUrlEncode()

        val url = "$serverUrl/$mainPath/$apiName?$queryParams"

        loadUrlInBrowser(url)
    }

    inline fun <reified T> T.toParameterString(): String? {
        if (this == null) {
            return null
        }

        when (this) {
            is String -> return this
            is Enum<*> -> return this.name
            else -> error("not supported type $this")
        }
    }
}

expect val platform: ClientPlatform

expect fun loadUrlInBrowser(url: String)
expect val packageName: String?
