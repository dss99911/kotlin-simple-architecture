package kim.jeonghyeon.net

import java.net.ConnectException

actual fun Exception.isConnectException(): Boolean = this is ConnectException