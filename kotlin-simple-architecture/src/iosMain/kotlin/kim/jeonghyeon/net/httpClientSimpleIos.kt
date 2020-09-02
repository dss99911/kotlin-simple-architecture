package kim.jeonghyeon.net

import io.ktor.client.*

@HttpClientDsl
actual fun httpClientSimple(config: HttpClientConfig<*>.() -> Unit): HttpClient =
    httpClientDefault {
        //todo Logging is not woring on coroutine native-mt
//        install(Logging) {
//            logger = Logger.DEFAULT
//            level = LogLevel.ALL
//        }

        config()
    }


//todo check exception type
actual fun Throwable.isConnectException(): Boolean = false