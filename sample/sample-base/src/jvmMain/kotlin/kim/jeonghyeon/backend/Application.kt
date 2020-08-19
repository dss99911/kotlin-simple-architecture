package kim.jeonghyeon.backend

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.util.KtorExperimentalAPI
import kim.jeonghyeon.api.PreferenceController
import kim.jeonghyeon.auth.AuthenticationType
import kim.jeonghyeon.auth.SignFeature
import kim.jeonghyeon.backend.auth.SampleSignBasicController
import kim.jeonghyeon.backend.auth.SampleSignDigestController
import kim.jeonghyeon.backend.auth.SampleSignJwtController
import kim.jeonghyeon.backend.controller.SampleController
import kim.jeonghyeon.backend.di.SampleServiceLocatorImpl
import kim.jeonghyeon.backend.di.serviceLocator
import kim.jeonghyeon.backend.user.UserSessionController
import kim.jeonghyeon.backend.user.UserJwtController
import kim.jeonghyeon.net.AUTH_TYPE
import kim.jeonghyeon.net.SimpleRouting
import kim.jeonghyeon.net.addControllerBeforeInstallSimpleRouting
import kim.jeonghyeon.onApplicationCreate

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@KtorExperimentalAPI
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    val config = environment.config
    val serviceConfig = config.config("service")
    val mode = serviceConfig.property("environment").getString()
    val production = mode == "production"

    log.info("Environment: $mode")

    onApplicationCreate(SampleServiceLocatorImpl())

    installSignFeature(AUTH_TYPE)


    install(SimpleRouting) {
        +SampleController()
        +PreferenceController(serviceLocator.preference)
        logging = !production
    }

}

//todo wrap all the simple architecture feature to one feature.

fun Application.installSignFeature(authType: AuthenticationType) {
    install(SignFeature) {
        when (authType) {
            AuthenticationType.JWT -> {
                jwt {
                    algorithm = serviceLocator.jwtAlgorithm
                    controller = SampleSignJwtController()
                }
                addControllerBeforeInstallSimpleRouting(UserJwtController())
            }
            AuthenticationType.BASIC -> {
                basic {
                    controller = SampleSignBasicController()
                }
                addControllerBeforeInstallSimpleRouting(UserSessionController())
            }
            AuthenticationType.DIGEST -> {
                digest {
                    controller = SampleSignDigestController()
                }
                addControllerBeforeInstallSimpleRouting(UserSessionController())
            }
        }
    }
}