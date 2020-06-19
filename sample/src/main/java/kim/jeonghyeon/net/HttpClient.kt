package kim.jeonghyeon.net

import io.ktor.client.HttpClient
import kim.jeonghyeon.common.net.clientAndroid

actual val client: HttpClient get() = clientAndroid