package kim.jeonghyeon.auth

import kim.jeonghyeon.androidlibrary.extension.ctx
import kim.jeonghyeon.androidlibrary.extension.startActivityUrl

actual fun loadUrlInBrowser(url: String) {
    startActivityUrl(url)
}

actual val platform: ClientPlatform = ClientPlatform.ANDROID

actual val packageName: String? get() = ctx.packageName
