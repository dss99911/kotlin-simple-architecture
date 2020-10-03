package kim.jeonghyeon.auth

import kim.jeonghyeon.application
import platform.Foundation.NSURL

actual fun loadUrlInBrowser(url: String) {
    application.openURL(NSURL(string = url))
}

actual val platform: ClientPlatform = ClientPlatform.IOS

//todo if this is null, facebook on ios not working.
// if url end with 'packageName=' then, it's working on android but error on ios.
// so added "null" to make 'packageName=null'
// but, I think if url is not ended =. then it will work. so, change order of parameters or just don't add packageName parameter
actual val packageName: String? = "null"
