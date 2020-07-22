package kim.jeonghyeon.common.net

import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import kim.jeonghyeon.androidlibrary.extension.isDebug

val clientAndroid = httpClientDefault {
    if (isDebug) {
        install(Logging) {
            logger = AndroidLogger()
            level = LogLevel.ALL
        }
    }
}

private class AndroidLogger : Logger {
    override fun log(message: String) {
        message.split("\n").forEach {
            kim.jeonghyeon.util.log(it)
        }

    }
}