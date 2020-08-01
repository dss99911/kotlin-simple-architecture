package kim.jeonghyeon.net

import io.ktor.client.HttpClient
import io.ktor.client.features.logging.DEFAULT
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import kim.jeonghyeon.common.net.httpClientDefault

actual val client: HttpClient
    get() = httpClientDefault {
        //todo move to library and also seperate iosDebug, iosRelease if possible
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }
    }