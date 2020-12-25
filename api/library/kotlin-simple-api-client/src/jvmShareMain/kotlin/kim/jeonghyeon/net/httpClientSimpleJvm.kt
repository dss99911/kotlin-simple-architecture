package kim.jeonghyeon.net

import io.ktor.client.*
import io.ktor.client.features.logging.*
import java.net.ConnectException

actual fun Throwable.isConnectException(): Boolean = this is ConnectException
