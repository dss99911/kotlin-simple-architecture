package kim.jeonghyeon.sample.etc.web

import android.webkit.WebView

fun log() {
    WebView.setWebContentsDebuggingEnabled(true)
    //you can debug on chrome browser on more tools -> remote devices
}

fun loadFileFromAsset(webView: WebView) {
    webView.loadUrl("file:///android_asset/filename.html")
}