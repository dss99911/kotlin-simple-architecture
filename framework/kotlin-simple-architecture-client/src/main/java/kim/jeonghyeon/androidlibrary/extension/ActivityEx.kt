@file:Suppress("unused")

package kim.jeonghyeon.androidlibrary.extension

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import kim.jeonghyeon.extension.alsoIfFalse
import kim.jeonghyeon.extension.alsoIfTrue


@SuppressLint("ObsoleteSdkInt")
fun Activity.setStatusBarTransparent() {
    if (isFromVersion(Build.VERSION_CODES.LOLLIPOP)) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
    }
}

inline fun <reified T : Activity> Context.startActivity(): Boolean {
    val intent = Intent(this, T::class.java)
    return checkAndStartActivity(intent)
}

fun Context.checkAndStartActivity(intent: Intent): Boolean {
    if (this !is Activity) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    return hasActivity(intent).alsoIfTrue { startActivity(intent) }
}

fun Context.startActivity(action: String): Boolean {
    val intent = Intent(action)
    return checkAndStartActivity(intent)
}

inline fun <reified T : Activity> Context.startActivity(flags: Int): Boolean {
    return checkAndStartActivity(Intent(this, T::class.java)
            .apply { this.flags = flags })
}

inline fun <reified T : Activity> startActivity(): Boolean {
    return startActivity(intent(T::class))
}

fun startActivity(intent: Intent): Boolean {
    return ctx.checkAndStartActivity(intent)
}

fun startActivityMailTo(address: String, subject: String, body: String): Boolean {
    val uriString = "mailto:$address?subject=${Uri.encode(subject)}&body=${Uri.encode(body)}"
    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse(uriString))
    return startActivity(intent)
}

fun startActivitySendText(text: String): Boolean {
    val sharingIntent = Intent(Intent.ACTION_SEND)
    sharingIntent.type = "text/plain"
    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, text)
    return startActivity(Intent.createChooser(sharingIntent, "Share"))
}

fun startActivityMarket(packageName: String): Boolean {
    return startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
        .alsoIfFalse {
            return startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                )
            )
        }
}

fun startAppDetailSetting(packageName: String) =
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }.let { startActivity(it) }

fun startActivityUrl(url: String): Boolean {
    return startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
}

inline fun <reified T : Activity> startActivity(flags: Int): Boolean {
    val intent = intent(T::class).apply { this.flags = flags }
    return startActivity(intent)
}

fun hasActivity(intent: Intent): Boolean {
    return intent.resolveActivity(ctx.packageManager) != null
}