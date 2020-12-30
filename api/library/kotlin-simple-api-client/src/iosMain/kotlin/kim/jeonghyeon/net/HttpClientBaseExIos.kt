package kim.jeonghyeon.net

import io.ktor.client.engine.ios.*
import io.ktor.util.*

//todo check exception type
@OptIn(KtorExperimentalAPI::class)
actual fun Throwable.isConnectException(): Boolean = this is IosHttpRequestException