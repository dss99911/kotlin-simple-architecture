@file:Suppress("unused")

package kim.jeonghyeon.androidlibrary.extension

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import kim.jeonghyeon.kotlinlibrary.extension.onFalse
import kim.jeonghyeon.kotlinlibrary.extension.onTrue


@SuppressLint("ObsoleteSdkInt")
fun Activity.setStatusBarTransparent() {
    if (isFromVersion(Build.VERSION_CODES.LOLLIPOP)) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
    }
}

/**
 * if not granted, request permission. and return false
 */
fun Activity.checkPermission(permission: String, requestCode: Int): Boolean {
    val granted = ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

    if (!granted) {
        // We don't have permission so prompt the user
        ActivityCompat.requestPermissions(
                this,
                arrayOf(permission),
                requestCode
        )
    }

    return granted
}

inline fun <reified T : Activity> Context.startActivity(): Boolean {
    val intent = Intent(this, T::class.java)
    return checkAndStartActivity(intent)
}

fun Context.checkAndStartActivity(intent: Intent): Boolean {
    if (this !is Activity) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    return hasActivity(intent).onTrue { startActivity(intent) }
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
            .onFalse { return startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName"))) }
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

fun Activity.createProgressDialog(): AlertDialog = AlertDialog.Builder(this)
        .setView(ProgressBar(this))
        .setCancelable(false)
        .create()
        .apply { window?.setBackgroundDrawable(ColorDrawable(0)) }

fun Fragment.createProgressDialog(): AlertDialog = AlertDialog.Builder(context)
    .setView(ProgressBar(context))
    .setCancelable(false)
    .create()
    .apply { window?.setBackgroundDrawable(ColorDrawable(0)) }

fun Activity.buildListDialog(title: String, items: Array<String>, itemClickListener: (dialog:DialogInterface, which: Int) -> Unit): AlertDialog.Builder =
        AlertDialog.Builder(this)
                .setTitle(title)
                .setAdapter(ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice).apply { addAll(*items) }, itemClickListener)

fun Fragment.buildListDialog(title: String, items: Array<String>, itemClickListener: (dialog:DialogInterface, which: Int) -> Unit): AlertDialog.Builder? {
    val context = this.context?:return null
    return AlertDialog.Builder(context)
        .setTitle(title)
        .setAdapter(ArrayAdapter<String>(context, android.R.layout.select_dialog_singlechoice).apply { addAll(*items) }, itemClickListener)
}