package kim.jeonghyeon.auth

import android.content.Intent
import android.net.Uri
import kim.jeonghyeon.androidlibrary.extension.ctx

actual fun loadUrlInBrowser(url: String) {
    ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    })
}

actual val platform: ClientPlatform = ClientPlatform.ANDROID

actual val packageName: String? get() = ctx.packageName
