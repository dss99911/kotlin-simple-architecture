package kim.jeonghyeon.auth

import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.auth.*
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.features.origin
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.util.*
import kim.jeonghyeon.db.User
import kim.jeonghyeon.di.serviceLocator
import kim.jeonghyeon.extension.toJsonObject
import kim.jeonghyeon.util.log

abstract class SignInAuthConfiguration(internal val signInAuthType: SignInAuthType) {
    internal abstract fun getController(): SignController
    internal abstract fun initialize(pipeline: Application)

    internal fun getUser(signId: String, oAuthName: OAuthServerName? = null): User? {
        return serviceLocator.userQueries.selectOneBySignIdAndAuthType(signId, signInAuthType.authName, oAuthName?.name).executeAsOneOrNull()
    }
}

class SignBasicConfiguration(var controller: SignBasicController? = null) : SignInAuthConfiguration(SignInAuthType.BASIC) {

    override fun initialize(pipeline: Application): Unit = with(pipeline) {
        authentication {
            basic(signInAuthType.authName) {
                validate { credentials ->
                    validateUser(credentials)
                }
            }
        }
    }

    override fun getController(): SignController = controller?: SignBasicController()

    private suspend fun validateUser(credentials: UserPasswordCredential): Principal? {
        val user = getUser(credentials.name) ?: return null
        return if (user.password == digest(credentials.password)) {
            UserIdPrincipal(user.id.toString())
        } else {
            null
        }
    }
}

class SignDigestConfiguration(var controller: SignDigestController? = null) :
    SignInAuthConfiguration(SignInAuthType.DIGEST) {
    @Suppress("UNUSED_ANONYMOUS_PARAMETER")
    @OptIn(KtorExperimentalAPI::class)
    override fun initialize(pipeline: Application): Unit = with(pipeline) {
        authentication {
            digest(signInAuthType.authName) {
                realm = REALM_SIMPLE_API

                digestProvider { userName, realm ->
                    //todo check realm?
                    hex(getUser(userName)?.password?:return@digestProvider null)
                }
            }
        }
    }

    override fun getController(): SignController = controller ?: SignDigestController()
}

class SignOAuthConfiguration(
    var controller: SignOAuthController? = null
) : SignInAuthConfiguration(SignInAuthType.OAUTH) {

    override fun initialize(pipeline: Application): Unit = with(pipeline) {
        hasOAuth = true
        authentication {
            oauth(signInAuthType.authName) {
                client = HttpClient(Apache) {
                    install(Logging) {
                        logger = Logger.DEFAULT
                        level = LogLevel.ALL
                    }
                }
                providerLookup = { findOAuthSettings() }
                urlProvider = { findRedirectionUrl() ?: error("platform is not inappropriate") }
            }
        }
    }

    fun add(name: OAuthServerName, settings: OAuthSettings) {
        oauths[name] = settings
    }

    fun google(
        clientId: String,
        clientSecret: String,
        defaultScopes: List<String> = listOf("profile")// no email, but gives full name, picture, and id
    ) {
        check(defaultScopes.contains("profile")) { "google oauth requires profile scope" }

        add(
            OAuthServerName.GOOGLE,
            OAuthSettings(
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "google",
                    authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
                    accessTokenUrl = "https://www.googleapis.com/oauth2/v3/token",
                    requestMethod = HttpMethod.Post,
                    clientId = clientId,
                    clientSecret = clientSecret,
                    defaultScopes = defaultScopes
                ),
                { accessToken ->
                    get("https://www.googleapis.com/userinfo/v2/me") {
                        header(HttpHeaders.Authorization, "Bearer $accessToken")
                    }
                },
                "id"
            )

        )
    }

    fun facebook(
        clientId: String,
        clientSecret: String,
        defaultScopes: List<String> = listOf("public_profile")// no email, but gives full name, picture, and id
    ) {
        check(defaultScopes.contains("public_profile")) { "facebook oauth requires profile scope" }

        add(
            OAuthServerName.FACEBOOK,
            OAuthSettings(
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "facebook",
                    authorizeUrl = "https://www.facebook.com/v8.0/dialog/oauth",
                    accessTokenUrl = "https://graph.facebook.com/v8.0/oauth/access_token",
                    requestMethod = HttpMethod.Get,
                    clientId = clientId,
                    clientSecret = clientSecret,
                    defaultScopes = defaultScopes
                ),
                { accessToken ->
                    get("https://graph.facebook.com/v8.0/me") {
                        parameter("access_token", accessToken)
                    }
                },
                "id"
            )

        )
    }

    private fun ApplicationCall.findOAuthSettings(): OAuthServerSettings.OAuth2ServerSettings? {
        val serverName = request.queryParameters[QUERY_OAUTH_SERVER_NAME]?.toJsonObject<OAuthServerName>() ?: return null
        return oauths[OAuthServerName(serverName.name)]?.serverSettings
    }

    private fun ApplicationCall.findRedirectionUrl(): String? {
        //get server url from client, because server doesn't know entry point of server.
        //malicious attacker may change this value. but OAuth server validate the redirect url's correction

        //making redirection uri with same parameter which client requested
        // can't use request.uri because when calling api of access token, the uri includes state and code
        val queryParams = listOf(
            QUERY_SERVER_URL,
            QUERY_OAUTH_SERVER_NAME,
            QUERY_REDIRECT_URL,
            QUERY_PLATFORM,
            QUERY_PACKAGE_NAME
        ).map { it to request.queryParameters[it] }.formUrlEncode()


        return "${request.queryParameters[QUERY_SERVER_URL]}${request.path()}?$queryParams"
    }

    override fun getController(): SignController {
        return controller ?: SignOAuthController()
    }

    companion object {
        internal var hasOAuth: Boolean = false
        internal val oauths: MutableMap<OAuthServerName, OAuthSettings> = mutableMapOf()
    }

    data class OAuthSettings(
        val serverSettings: OAuthServerSettings.OAuth2ServerSettings,
        val getProfile: suspend HttpClient.(accessToken: String) -> String,
        val idKey: String
    )
}