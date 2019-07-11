package kim.jeonghyeon.androidlibrary.service

import android.app.Activity
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import kim.jeonghyeon.androidlibrary.R
import kim.jeonghyeon.androidlibrary.extension.*
import kim.jeonghyeon.kotlinlibrary.extension.catchReturnNull
import kim.jeonghyeon.kotlinlibrary.type.WeakArrayList
import org.jetbrains.anko.accessibilityManager

object BaseAccessibilityManager : BroadcastReceiver() {
    private val listeners = WeakArrayList<AccessibilityManager.AccessibilityStateChangeListener>()

    init {
        ctx.registerReceiver(this, intentFilter(BaseAccessibilityService.ACTION_SERVICE_CONNECTED, BaseAccessibilityService.ACTION_SERVICE_DISCONNECTED))
    }

    fun addAccessibilityStateChangeListener(listener: AccessibilityManager.AccessibilityStateChangeListener) {
        listeners.addWeakReference(listener)
    }

    fun removeAccessibilityStateChangeListener(listener: AccessibilityManager.AccessibilityStateChangeListener) {
        listeners.removeWeakReference(listener)
    }

    fun startAccessibilitySettingWithGuide(activity: Activity) {
        val otherAccessibilityIds = ctx.accessibilityManager.getEnabledAccessibilityIds()
                .filter { !it.contains(ctx.packageName) }

        if (otherAccessibilityIds.isEmpty()) {
            startAccessibilitySetting(activity)
            return
        }

        showGuideDialog(activity, otherAccessibilityIds.toSet())
    }

    private fun showGuideDialog(activity: Activity, otherAppIds: Set<String>) {
        //show dialog
        val otherAppNamesString = otherAppIds.joinToString(", ") {
            catchReturnNull { ctx.packageManager
                    .getApplicationInfo(it.split("/")[0], 0) }
                    ?.loadLabel(ctx.packageManager)?:"Unknown App"
        }

        AlertDialog.Builder(activity)
                .setTitle(R.string.title_accessibility_guide)
                .setMessage(ctx.getString(R.string.message_accessibility_guide, otherAppNamesString))
                .setPositiveButton(android.R.string.ok) { dialog, _ ->
                    dialog.dismissWithoutException()
                }.setOnDismissListener{
                    startAccessibilitySetting(activity)
                }.create()
                .showWithoutException()
    }

    private fun startAccessibilitySetting(context: Context) {
        context.startActivity(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        toastLong(ctx.getString(R.string.toast_turn_on_acc, app.name))
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        listeners.forEachWeakReference { it.onAccessibilityStateChanged(intent?.action == BaseAccessibilityService.ACTION_SERVICE_CONNECTED) }
    }

}
