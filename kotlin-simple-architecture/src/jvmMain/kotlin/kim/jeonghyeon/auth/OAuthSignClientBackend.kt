package kim.jeonghyeon.auth

actual fun loadUrlInBrowser(url: String) {
    error("not support backend oauth client")
}

actual val platform: ClientPlatform get() = error("not support backend oauth client")

actual val packageName: String? = null
