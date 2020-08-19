package kim.jeonghyeon.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTCreator
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.digest
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.auth.jwt.jwt
import io.ktor.request.uri
import io.ktor.response.header
import kim.jeonghyeon.db.User
import kim.jeonghyeon.net.call

lateinit var jwtAlgorithm: Algorithm


open class SignJwtController : SignDigestController() {

    override suspend fun signOut() {
        call().response.header(HEADER_NAME_TOKEN, "")
    }

    override suspend fun putTokenToResponse(user: User) {
        call().response.header(HEADER_NAME_TOKEN, createJwtToken(user.id))
    }

    private fun createJwtToken(id: String): String {
        return JWT.create().withJWTId(id).apply { onJwtTokenBuild(id) }.sign(jwtAlgorithm)
    }

    /**
     * override and add additional claims.
     */
    open fun JWTCreator.Builder.onJwtTokenBuild(userId: String) {

    }
}

/**
 * this use Digest Authentication for signUp and signIn and use Jwt token for authentication
 */
class SignJwtConfiguration(var controller: SignJwtController? = null, var algorithm: Algorithm? = null) : SignAuthConfiguration(AuthenticationType.JWT) {
    override fun initialize(pipeline: Application):Unit = with(pipeline) {
        jwtAlgorithm = algorithm!!

        install(Authentication) {
            digest(AuthenticationType.DIGEST.name) {
                realm = REALM_SIMPLE_API
                digestProvider { userName, realm ->
                    validateUserDigest(userName)
                }
                skipWhen {
                    !it.request.uri.isNonceApiUri() && !it.request.uri.isSignInApiUri()
                }
            }

            jwt(AuthenticationType.JWT.name) {
                verifier(makeJwtVerifier())

                validate { credential -> JWTPrincipal(credential.payload) }

                skipWhen {

                    it.request.uri.isNonceApiUri() || it.request.uri.isSignInApiUri()
                }
            }

        }
    }

    override fun getController(): SignController {
        return controller?:SignJwtController()
    }
}

private fun makeJwtVerifier(): JWTVerifier = JWT.require(jwtAlgorithm).build()

private fun String.isNonceApiUri(): Boolean {
    return this == "/" + SignDigestApi::class.qualifiedName!!.replace(
        ".",
        "/"
    ) + "/" + SignDigestApi::getNonce.name
}

private fun String.isSignInApiUri(): Boolean {
    return this == "/" + SignDigestApi::class.qualifiedName!!.replace(
        ".",
        "/"
    ) + "/" + "signIn"
}
