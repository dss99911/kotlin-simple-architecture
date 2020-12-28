package kim.jeonghyeon.net

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.HttpClientDsl
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import kim.jeonghyeon.androidlibrary.extension.isDebug
import java.net.ConnectException

@HttpClientDsl
actual fun httpClientSimple(config: HttpClientConfig<*>.() -> Unit): HttpClient =
    httpClientDefault {
        if (isDebug) {
            install(Logging) {
                logger = AndroidLogger()
                level = LogLevel.ALL
            }
        }

        config()
    }

private class AndroidLogger : Logger {
    override fun log(message: String) {
        message.split("\n").forEach {
            kim.jeonghyeon.util.log.d(it)
        }

    }
}
