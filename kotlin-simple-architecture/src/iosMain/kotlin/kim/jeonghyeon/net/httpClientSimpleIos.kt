package kim.jeonghyeon.net

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.HttpClientDsl
import io.ktor.client.features.logging.DEFAULT
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging

@HttpClientDsl
actual fun httpClientSimple(config: HttpClientConfig<*>.() -> Unit): HttpClient =
    httpClientDefault {
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }

        config()
    }


//todo check exception type
actual fun Exception.isConnectException(): Boolean = false