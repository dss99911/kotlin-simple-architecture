package kim.jeonghyeon.androidlibrary.extension

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.app.ActivityManager
import android.app.Service
import android.app.job.JobScheduler
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.view.accessibility.AccessibilityManager
import androidx.annotation.RequiresApi
import org.jetbrains.anko.accessibilityManager


@Suppress("DEPRECATION")
fun <T : Service> isServiceOn(service: Class<T>): Boolean {
    val manager = ctx.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    return manager.getRunningServices(Integer.MAX_VALUE)
            .any {
                service.name == it.service.className
                        && ctx.packageName == it.service.packageName
            }
}

fun <T : AccessibilityService> AccessibilityManager.isAccessibilityOn(service: Class<T>) =
        if (isServiceOn(service)) isAccessibilitySettingOn()
        else false

fun AccessibilityManager.isAccessibilitySettingOn(): Boolean =
        Settings.Secure.getString(ctx.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
                ?.contains(ctx.packageName, true)?:false ||
        getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
                .filter { it.id.contains(ctx.packageName) }
                .any()

fun AccessibilityManager.getEnabledAccessibilityIds(): Array<String> =
        getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
                .map { it.id }
                .toTypedArray()

val jobScheduler
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    get() = ctx.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler


/**
 * get default launcher app.
 */
fun PackageManager.getDefaultLauncherPackageName(): String? {

    val activityInfo = Intent(Intent.ACTION_MAIN).apply {
        addCategory(Intent.CATEGORY_HOME)
    }.let {
        resolveActivity(it, 0)
    }.activityInfo

    return when {
        activityInfo == null -> null // should not happen. A home is always installed, isn't it?
        "android" == activityInfo.packageName -> null// No default selected
        else -> activityInfo.packageName
        // res.activityInfo.packageName and res.activityInfo.name gives you the default app
    }
}

fun PackageManager.isPackageInstalled(packageName: String): Boolean {
    try {
        getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
        return true
    } catch (e: PackageManager.NameNotFoundException) {
    }
    return false
}