package kim.jeonghyeon.backend

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import kim.jeonghyeon.SimpleFeature
import kim.jeonghyeon.api.PreferenceController
import kim.jeonghyeon.auth.JwtServiceAuthConfiguration
import kim.jeonghyeon.auth.ServiceAuthType
import kim.jeonghyeon.auth.SessionServiceAuthConfiguration
import kim.jeonghyeon.auth.SignInAuthType
import kim.jeonghyeon.backend.auth.SampleSignBasicController
import kim.jeonghyeon.backend.auth.SampleSignDigestController
import kim.jeonghyeon.backend.auth.SampleSignOAuthController
import kim.jeonghyeon.backend.controller.SampleController
import kim.jeonghyeon.backend.di.ServiceLocatorBackendImpl
import kim.jeonghyeon.backend.di.serviceLocatorBackend
import kim.jeonghyeon.backend.user.UserController
import kim.jeonghyeon.net.AUTH_TYPE_SERVICE
import kim.jeonghyeon.net.AUTH_TYPE_SIGN_IN

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@KtorExperimentalAPI
@kotlin.jvm.JvmOverloads
fun Application.module(@Suppress("UNUSED_PARAMETER") testing: Boolean = false) {

    install(SimpleFeature) {
        serviceLocator = ServiceLocatorBackendImpl(this@module).also { serviceLocatorBackend = it }

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
                else -> { }
            }

            //I added all authentication example
            // use one of this
            serviceAuthConfig = when (AUTH_TYPE_SERVICE) {
                ServiceAuthType.SESSION -> SessionServiceAuthConfiguration()
                ServiceAuthType.JWT -> JwtServiceAuthConfiguration(serviceLocatorBackend.jwtAlgorithm)
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
            +PreferenceController(serviceLocatorBackend.preference)
            +UserController()

            configure = {
                get("/ping") {
                    call.respondText("true")
                }
            }
        }
    }

}

//todo code generator from application.conf
// consider value can be changed on production.
@OptIn(KtorExperimentalAPI::class)
val Application.googleClient get() = environment.config.config("oauth.google")
@OptIn(KtorExperimentalAPI::class)
val Application.googleClientId get() = googleClient.property("clientId").getString()
@OptIn(KtorExperimentalAPI::class)
val Application.googleClientSecret get() = googleClient.property("clientSecret").getString()
@OptIn(KtorExperimentalAPI::class)
val Application.facebookClient get() = environment.config.config("oauth.facebook")
@OptIn(KtorExperimentalAPI::class)
val Application.facebookClientId get() = facebookClient.property("clientId").getString()
@OptIn(KtorExperimentalAPI::class)
val Application.facebookClientSecret get() = facebookClient.property("clientSecret").getString()
@OptIn(KtorExperimentalAPI::class)
val Application.dbPath get() = environment.config.property("dbPath").getString()
@OptIn(KtorExperimentalAPI::class)
val Application.jwtSecret get() = environment.config.property("jwtSecret").getString()