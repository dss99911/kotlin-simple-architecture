package kim.jeonghyeon.backend

import io.ktor.application.*
import io.ktor.util.*
import kim.jeonghyeon.SimpleFeature

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@KtorExperimentalAPI
@kotlin.jvm.JvmOverloads
fun Application.module(@Suppress("UNUSED_PARAMETER") testing: Boolean = false) {
    install(SimpleFeature) {
        routing {
            +GreetingController()
        }
    }
}