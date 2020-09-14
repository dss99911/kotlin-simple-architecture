package kim.jeonghyeon.auth

import kim.jeonghyeon.application
import platform.Foundation.NSURL

actual fun loadUrlInBrowser(url: String) {
    application.openURL(NSURL(string = url))
}

actual val platform: ClientPlatform = ClientPlatform.IOS

actual val packageName: String? = null
