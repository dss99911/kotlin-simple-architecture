package kim.jeonghyeon.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTCreator
import io.ktor.application.*
import io.ktor.auth.OAuthAccessTokenResponse
import io.ktor.auth.UserIdPrincipal
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.Url
import io.ktor.http.fullPath
import io.ktor.response.header
import io.ktor.response.respondRedirect
import io.ktor.util.*
import kim.jeonghyeon.db.User
import kim.jeonghyeon.db.UserQueries
import kim.jeonghyeon.di.serviceLocator
import kim.jeonghyeon.jvm.extension.toJsonObject
import kim.jeonghyeon.net.*
import kim.jeonghyeon.net.ControllerUtil.attributes
import kim.jeonghyeon.net.ControllerUtil.authentication
import kim.jeonghyeon.net.ControllerUtil.call
import kim.jeonghyeon.net.ControllerUtil.sessions
import kim.jeonghyeon.net.ControllerUtil.setResponded
import kim.jeonghyeon.net.error.ApiError
import kim.jeonghyeon.net.error.ApiErrorBody
import kim.jeonghyeon.net.error.errorApi

/**
 * simplest authentication
 */
open class SignBasicController(
    userQueries: UserQueries = serviceLocator.userQueries
) : SignController(userQueries, SignInAuthType.BASIC), SignBasicApi {
    /**
     * override and add additional data on extra.
     */
    final override suspend fun signUp(signId: String, password: String, extra: String?) {
        val digestedPassword = digest(password)

        getUser(signId)?.also {
            errorApi(ApiErrorBody.idAlreadyExists)
        }

        if (!validateSignUp(signId, extra)) {
            errorApi(ApiErrorBody.invalidSignUpRequest)
        }

        insertUser(signId, digestedPassword, makeUserExtraOnSignUp(signId, extra))

        onUserCreated(getUser(signId)!!, extra)
    }
}


/**
 * user doesn't expose user's password to server
 * but, need to call one more api 'getNonce' when signIn
 */
open class SignDigestController(
    userQueries: UserQueries = serviceLocator.userQueries
) : SignController(userQueries, SignInAuthType.DIGEST), SignDigestApi {
    final override suspend fun signUpHashed(signId: String, ha1: String, extra: String?) {
        getUser(signId)?.also {
            errorApi(ApiErrorBody.idAlreadyExists)
        }

        if (!validateSignUp(signId, extra)) {
            errorApi(ApiErrorBody.invalidSignUpRequest)
        }

        insertUser(signId, ha1, makeUserExtraOnSignUp(signId, extra))

        onUserCreated(getUser(signId)!!, extra)
    }

    final override suspend fun getNonce() {

    }
}

open class SignOAuthController(
    userQueries: UserQueries = serviceLocator.userQueries
) : SignController(userQueries, SignInAuthType.OAUTH), SignOAuthApi {

    /**
     * if you want to sign up and sign in at the same time, override this function
     * and use [signUpAndInAtSameTime]
     *
     */
    override suspend fun signUp(
        platform: ClientPlatform,
        oAuthServerName: OAuthServerName,
        redirectUrl: String,
        packageName: String?
    ) {
        fetchSignData(oAuthServerName)

        getUser(attributes()[ATTRIBUTE_KEY_SIGN_ID], oAuthServerName)?.also {
            errorApi(ApiErrorBody.idAlreadyExists)
        }

        validateAndCreateUser(oAuthServerName)

        respondByPlatform(platform, null, redirectUrl, packageName)
    }

    final override suspend fun signIn(
        platform: ClientPlatform,
        oAuthServerName: OAuthServerName,
        redirectUrl: String,
        packageName: String?
    ) {
        fetchSignData(oAuthServerName)

        val user = getUser(attributes()[ATTRIBUTE_KEY_SIGN_ID], oAuthServerName) ?: throw ApiError(
            ApiErrorBody.credentialInvalid
        )

        respondByPlatform(platform, createToken(user), redirectUrl, packageName)
    }

    /**
     * If user already Signed up, then just sign in,
     * If user is not yet signed up, then sign up and sign in
     */
    protected suspend fun signUpAndInAtSameTime(
        platform: ClientPlatform,
        oAuthServerName: OAuthServerName,
        redirectUrl: String,
        packageName: String?
    ) {
        fetchSignData(oAuthServerName)

        var user = getUser(attributes()[ATTRIBUTE_KEY_SIGN_ID], oAuthServerName)

        if (user == null) {
            user = validateAndCreateUser(oAuthServerName)
        }

        respondByPlatform(platform, createToken(user), redirectUrl, packageName)
    }

    /**
     * make extra of user table
     */
    open suspend fun fetchExtraFromOAuthServerOnSignUp(
        oAuthServerName: OAuthServerName,
        signId: String,
        accessToken: String,
        idMap: Map<String, String?>
    ): String? = null

    private suspend fun fetchSignData(oAuthServerName: OAuthServerName) {
        attributes().getOrNull(ATTRIBUTE_KEY_ACCESS_TOKEN)?.let {
            return
        }

        val principal = authentication().principal<OAuthAccessTokenResponse.OAuth2>()
            ?: throw ApiError(ApiErrorBody.credentialInvalid)

        principal.accessToken.let {
            attributes().put(ATTRIBUTE_KEY_ACCESS_TOKEN, it)
        }

        val oAuthSettings = SignOAuthConfiguration.oauths[oAuthServerName]!!

        val idJson = SignOAuthConfiguration.oauths[oAuthServerName]!!.run {
            HttpClient(Apache).getProfile(principal.accessToken)
        }.also { attributes().put(ATTRIBUTE_KEY_ID_JSON, it) }

        @Suppress("UNUSED_VARIABLE")
        val signId = idJson.toJsonObject()[oAuthSettings.idKey].asString.also {
            attributes().put(ATTRIBUTE_KEY_SIGN_ID, it)
        }
    }

    private suspend fun validateAndCreateUser(oAuthServerName: OAuthServerName): User {
        val signId = attributes()[ATTRIBUTE_KEY_SIGN_ID]

        val extra = fetchExtraFromOAuthServerOnSignUp(
            oAuthServerName,
            attributes()[ATTRIBUTE_KEY_SIGN_ID],
            attributes()[ATTRIBUTE_KEY_ACCESS_TOKEN],
            attributes()[ATTRIBUTE_KEY_ID_JSON].toJsonObject<Map<String, String?>>()
        )
        if (!validateSignUp(signId, extra)) {
            errorApi(ApiErrorBody.invalidSignUpRequest)
        }

        return createUser(oAuthServerName, extra)
    }

    private suspend fun createUser(oAuthServerName: OAuthServerName, extra: String?): User {
        val signId = attributes()[ATTRIBUTE_KEY_SIGN_ID]

        //oauth doesn't support extra.
        insertUser(signId, null, makeUserExtraOnSignUp(signId, extra), oAuthServerName)
        val user = getUser(signId, oAuthServerName)!!
        onUserCreated(user, extra)
        return user
    }

    private suspend fun createToken(user: User): String =
        if (selectedServiceAuthType == ServiceAuthType.SESSION) {
            sessionIdProvider.tracker.store(
                call(),
                UserSession(
                    user.id,
                    mutableMapOf<String, String>().apply { makeServiceAuthExtraOnSignIn(user) })
            )
        } else {
            createJwtToken(user)
        }

    private suspend fun respondByPlatform(
        platform: ClientPlatform,
        token: String?,
        redirectUrl: String,
        packageName: String?
    ) {
        if (attributes().getOrNull(ATTRIBUTE_KEY_RESPONDED) != null) {
            return
        }
        attributes().put(ATTRIBUTE_KEY_RESPONDED, Unit)

        val url = Url(redirectUrl)

        val tokenParameter =
            if (url.parameters.names().isEmpty()) "?$QUERY_TOKEN=$token" else "&$QUERY_TOKEN=$token"

        when (platform) {
            ClientPlatform.ANDROID -> {
                val scheme = url.protocol.name
                val defaultPort = if (scheme == "http") 80 else 443
                val port = url.port.let { port -> if (port == defaultPort || port == 0) "" else ":$port" }
                call().respondRedirect("intent://${url.host}$port${url.fullPath}$tokenParameter#Intent;scheme=$scheme;package=$packageName;end")
            }
            ClientPlatform.IOS, ClientPlatform.JS -> {
                call().respondRedirect("$redirectUrl$tokenParameter")
            }
        }

        setResponded()
    }

    protected val ATTRIBUTE_KEY_SIGN_ID = AttributeKey<String>("sign_id")
    protected val ATTRIBUTE_KEY_ID_JSON = AttributeKey<String>("id_json")
    protected val ATTRIBUTE_KEY_ACCESS_TOKEN = AttributeKey<String>("access_token")
    protected val ATTRIBUTE_KEY_RESPONDED = AttributeKey<Unit>("responded")
}

abstract class SignController(val userQueries: UserQueries, val authType: SignInAuthType) {

    suspend fun signIn(@Suppress("UNUSED_PARAMETER") authorization: String) {
        check(authType != SignInAuthType.OAUTH) {
            error("this function is not designed for OAuth")
        }

        val signId = authentication().principal<UserIdPrincipal>()?.name?: throw ApiError(ApiErrorBody.credentialInvalid)

        generateToken(signId)
    }

    /**
     * call this on [onUserCreated] if want to signIn on signUp
     */
    protected suspend fun generateToken(signId: String) {
        check(authType != SignInAuthType.OAUTH) {
            error("this function is not designed for OAuth")
        }

        val user = userQueries.selectOneBySignIdAndAuthType(signId, authType.authName , null).executeAsOneOrNull() ?: throw ApiError(ApiErrorBody.credentialInvalid)

        putTokenToResponse(user)
    }

    suspend fun signOut() {
        if (selectedServiceAuthType == ServiceAuthType.SESSION) {
            sessions().clear(HEADER_NAME_TOKEN)
        } else {
            call().response.header(HEADER_NAME_TOKEN, "")
        }

    }

    /**
     * After checking account not exists, check custom validation
     * If return false throw [ApiErrorBody.invalidSignUpRequest],
     * If need custom error, throw in this function.
     */
    open suspend fun validateSignUp(signId: String, extra: String?): Boolean = true


    /**
     * for saving extra when signup
     * or signIn directly
     */
    open suspend fun onUserCreated(user: User, extra: String?) {}

    /**
     * There are three extra
     * - extra : received from client
     * - userExtra : processed extra which will be saved on user table
     * - serviceAuthExtra : this is created on sign in, and whenever service api is called, able to get the extra without additional db query.
     * if extra should be indexed or need condition of extra while searching.
     *  save the extra data to additional table.
     *
     * so, if not required to save on userExtra. remove from userExtra
     */
    open suspend fun makeUserExtraOnSignUp(signId: String, extra: String?): String? {
        return extra
    }

    /**
     * override and add additional user data.
     * it's better to keep user data on session storage or token if the data is used frequently and no problem in security
     *
     */
    open fun MutableMap<String, String>.makeServiceAuthExtraOnSignIn(user: User) {

    }

    /**
     * override and add additional claims, like expiration
     * this is only for [ServiceAuthType.JWT]
     * for adding payload claim, use [makeServiceAuthExtraOnSignIn]
     * TODO consider to support header claim on configuration
     */
    open fun JWTCreator.Builder.onJwtTokenBuild(user: User) {}

    protected fun createJwtToken(user: User): String {
        return JWT.create()
            .withJWTId(user.id.toString())
            .apply { onJwtTokenBuild(user) }
            .apply { mutableMapOf<String,String>()
                .apply { makeServiceAuthExtraOnSignIn(user) }
                .forEach { (name, value) ->
                    withClaim(name, value)
                }
            }
            .sign(JwtServiceAuthConfiguration.jwtAlgorithm)
    }

    protected fun getUser(signId: String, oAuthName: OAuthServerName? = null): User? {
        return userQueries.selectOneBySignIdAndAuthType(signId, authType.authName, oAuthName?.name).executeAsOneOrNull()
    }


    protected fun insertUser(signId: String, password: String?, extra: String?, oAuthName: OAuthServerName? = null) {
        userQueries.insert(signId, password, authType.authName , oAuthName?.name, extra)
    }

    private suspend fun putTokenToResponse(user: User) {
        if (selectedServiceAuthType == ServiceAuthType.SESSION) {
            sessions().set(HEADER_NAME_TOKEN, UserSession(user.id, mutableMapOf<String, String>().apply { makeServiceAuthExtraOnSignIn(user)}))
        } else {
            call().response.header(HEADER_NAME_TOKEN, createJwtToken(user))
        }
    }
}