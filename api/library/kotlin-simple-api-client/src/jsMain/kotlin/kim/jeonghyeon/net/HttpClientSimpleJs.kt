package kim.jeonghyeon.net

import io.ktor.client.*
import io.ktor.client.features.logging.*

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
actual fun Throwable.isConnectException(): Boolean = false