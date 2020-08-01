package kim.jeonghyeon.backend

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.util.KtorExperimentalAPI
import kim.jeonghyeon.api.PreferenceController
import kim.jeonghyeon.backend.controller.SimpleController
import kim.jeonghyeon.backend.net.SimpleRouting

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

    install(SimpleRouting) {

        +SimpleController()
        +PreferenceController()//todo move to library.
        logging = !production
    }
}

lateinit var application: Application

val log get() = application.environment.log