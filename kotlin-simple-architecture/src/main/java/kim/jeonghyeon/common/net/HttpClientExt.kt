package kim.jeonghyeon.common.net

import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import kim.jeonghyeon.androidlibrary.extension.app
import kim.jeonghyeon.jvm.net.create


inline fun <reified API> api(baseUrl: String): API = clientAndroid.create(baseUrl)

val clientAndroid = httpClientDefault {
    if (!app.isProd || app.isDebug) {
        install(Logging) {
            logger = AndroidLogger()
            level = LogLevel.ALL
        }
    }
}

private class AndroidLogger : Logger {
    override fun log(message: String) {
        message.split("\n").forEach {
            kim.jeonghyeon.androidlibrary.util.Logger.log(it)
        }

    }
}