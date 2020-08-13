package kim.jeonghyeon.backend

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.basic
import io.ktor.sessions.*
import io.ktor.util.KtorExperimentalAPI
import kim.jeonghyeon.api.PreferenceController
import kim.jeonghyeon.backend.controller.SampleController
import kim.jeonghyeon.backend.di.serviceLocator
import kim.jeonghyeon.backend.net.SimpleRouting
import kim.jeonghyeon.backend.user.SignController
import kim.jeonghyeon.backend.user.UserController
import kim.jeonghyeon.backend.user.validateUser
import kim.jeonghyeon.net.USER_COOKIE_NAME
import kim.jeonghyeon.sample.User
import java.io.File

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@KtorExperimentalAPI
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    application = this

    val config = environment.config
    val serviceConfig = config.config("service")
    val mode = serviceConfig.property("environment").getString()
    log.info("Environment: $mode")
    val production = mode == "production"

    install(Authentication) {
        basic {
            validate { credentials ->
                validateUser(credentials)
            }
            skipWhen { it.sessions.get<User>() != null }
        }
    }

    install(SimpleRouting) {
        +SampleController()
        +PreferenceController(serviceLocator.preference)//todo move to library.
        +SignController()
        +UserController()
        logging = !production
    }

    //todo https://youtrack.jetbrains.com/issue/KTOR-912
    // Set-Cookie is attached on every api. and client save it again and again.
    install(Sessions) {
        cookie<User>(
            USER_COOKIE_NAME,
            directorySessionStorage(File(".sessions"), cached = true)
        ) {
            cookie.maxAgeInSeconds = 0
        }
    }
}

//todo move to library
lateinit var application: Application
//todo move to library
val log get() = application.environment.log