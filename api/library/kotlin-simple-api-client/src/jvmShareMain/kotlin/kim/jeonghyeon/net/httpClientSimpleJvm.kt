package kim.jeonghyeon.net

import java.net.ConnectException

actual fun Throwable.isConnectException(): Boolean = this is ConnectException
