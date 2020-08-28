package kim.jeonghyeon.backend

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import kim.jeonghyeon.SimpleFeature
import kim.jeonghyeon.api.PreferenceController
import kim.jeonghyeon.auth.*
import kim.jeonghyeon.backend.auth.SampleSignBasicController
import kim.jeonghyeon.backend.auth.SampleSignDigestController
import kim.jeonghyeon.backend.auth.SampleSignOAuthController
import kim.jeonghyeon.backend.controller.SampleController
import kim.jeonghyeon.backend.user.UserController
import kim.jeonghyeon.net.AUTH_TYPE_SERVICE
import kim.jeonghyeon.net.AUTH_TYPE_SIGN_IN

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@KtorExperimentalAPI
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    install(SimpleFeature) {
        serviceLocator = kim.jeonghyeon.backend.di.serviceLocator

        sign {
            //I added all authentication example
            // use one of this
            when (AUTH_TYPE_SIGN_IN) {
                SignInAuthType.BASIC -> {
                    basic {
                        controller = SampleSignBasicController()
                    }
                }
                SignInAuthType.DIGEST -> {
                    digest {
                        controller = SampleSignDigestController()
                    }
                }
            }

            //I added all authentication example
            // use one of this
            serviceAuthConfig = when (AUTH_TYPE_SERVICE) {
                ServiceAuthType.SESSION -> SessionServiceAuthConfiguration()
                ServiceAuthType.JWT -> JwtServiceAuthConfiguration(kim.jeonghyeon.backend.di.serviceLocator.jwtAlgorithm)
            }

            oauth {
                controller = SampleSignOAuthController()

                google(
                    googleClientId,
                    googleClientSecret
                )
                facebook(
                    facebookClientId,
                    facebookClientSecret
                )
            }
        }

        routing {
            +SampleController()
            +PreferenceController(kim.jeonghyeon.backend.di.serviceLocator.preference)
            +UserController()

            configure = {
                get("/ping") {
                    call.respondText("true")
                }
            }
        }
    }

}

val Application.googleClient get() = environment.config.config("oauth.google")
val Application.googleClientId get() = googleClient.property("clientId").getString()
val Application.googleClientSecret get() = googleClient.property("clientSecret").getString()
val Application.facebookClient get() = environment.config.config("oauth.facebook")
val Application.facebookClientId get() = facebookClient.property("clientId").getString()
val Application.facebookClientSecret get() = facebookClient.property("clientSecret").getString()