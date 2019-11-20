package kim.jeonghyeon.sample.web

import android.webkit.WebView

fun log() {
    WebView.setWebContentsDebuggingEnabled(true)
}

fun loadFileFromAsset(webView: WebView) {
    webView.loadUrl("file:///android_asset/filename.html");
}