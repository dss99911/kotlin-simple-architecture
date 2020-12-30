package kim.jeonghyeon.backend

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.util.*
import kim.jeonghyeon.net.SimpleRouting

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@KtorExperimentalAPI
@kotlin.jvm.JvmOverloads
fun Application.module(@Suppress("UNUSED_PARAMETER") testing: Boolean = false) {
    install(SimpleRouting) {
        +GreetingController()
    }

    install(ContentNegotiation) {
        gson {  }
    }
}