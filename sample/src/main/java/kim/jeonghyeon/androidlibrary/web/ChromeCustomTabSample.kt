package kim.jeonghyeon.androidlibrary.web

import android.content.Context
import android.graphics.Color
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

/**
 * Customize Chrome browser if url is lunched by Chrome. it is similar to webview, and also can open in browser in the settings.
 * require implementation 'com.android.support:customtabs:23.3.0'
 * https://chromium.googlesource.com/external/github.com/GoogleChrome/custom-tabs-client/+/08e2c9155aff7296428aae854769c30b4060ae88/README.md
 * todo 1. is it only work if chrome browser is default?
 */
object ChromeCustomTabSample {
    fun showCustomTab(context: Context, url: String) {
        val builder = CustomTabsIntent.Builder()
        builder.setToolbarColor(Color.BLUE);
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(context, Uri.parse(url))
    }
}