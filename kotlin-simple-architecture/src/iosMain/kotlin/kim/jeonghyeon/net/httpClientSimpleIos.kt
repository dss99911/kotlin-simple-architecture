package kim.jeonghyeon.net

import io.ktor.client.*
import io.ktor.client.engine.ios.*
import io.ktor.util.*

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
@OptIn(KtorExperimentalAPI::class)
actual fun Throwable.isConnectException(): Boolean = this is IosHttpRequestException