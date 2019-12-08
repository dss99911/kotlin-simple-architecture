package kim.jeonghyeon.androidlibrary.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Intent
import android.graphics.Path
import android.os.Build
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.CallSuper
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import kim.jeonghyeon.kotlinlibrary.extension.ignoreException

abstract class BaseAccessibilityService : AccessibilityService() {
    companion object {
        /**
         * if other accessibility service is turned on or accessibility service's process is different.
         * android.view.accessibility.AccessibilityManager.AccessibilityStateChangeListener listener is not invoked properly
         */
        const val ACTION_SERVICE_CONNECTED = "kim.jeonghyeon.androidlibrary.service.BaseAccessibilityService.ACTION_SERVICE_CONNECTED"
        const val ACTION_SERVICE_DISCONNECTED = "kim.jeonghyeon.androidlibrary.service.BaseAccessibilityService.ACTION_SERVICE_DISCONNECTED"
    }

    val eventLiveData = MutableLiveData<AccessibilityEvent>()
    private var eventPackageName: String? = null

    /**
     * in order to get current package name. canRetrieveWindowContent=true is required.
     * event.packageName is sometimes null when change app by tapping menu button two times.
     */
    val currentPackageName: String?
        get() {
            val rootPackageName = getRootView()?.packageName?.toString()
            return if (rootPackageName == "com.android.systemui" && eventPackageName != null) eventPackageName else rootPackageName
        }

    /**
     * this is used before this.onAccessibilityEvent is called in order to compare current and next package name
     */
    fun getNextPackageName(event: AccessibilityEvent?): String? {
        val rootPackageName = getRootView()?.packageName?.toString()
        return if (rootPackageName == "com.android.systemui" && event?.packageName != null) event.packageName.toString() else rootPackageName
    }

    private fun getRootView(): AccessibilityNodeInfo? = ignoreException { rootInActiveWindow }

    override fun onInterrupt() {
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        sendBroadcast(Intent(ACTION_SERVICE_CONNECTED))
    }


    override fun onUnbind(intent: Intent?): Boolean {
        sendBroadcast(Intent(ACTION_SERVICE_DISCONNECTED))
        return super.onUnbind(intent)
    }

    /**
     * require android:canPerformGestures="true"
     */
    @RequiresApi(Build.VERSION_CODES.N)
    fun tap(x: Float, y: Float) = ignoreException(defValue = false) {
        GestureDescription.Builder()
                .addStroke(GestureDescription.StrokeDescription(Path().apply { moveTo(x, y); lineTo(x, y) }, 0, 50))
                .build()
                .let { dispatchGesture(it, null, null) }
    }

    /**
     * require android:canPerformGestures="true"
     */
    @RequiresApi(Build.VERSION_CODES.N)
    fun drag(path: Path, time: Long) = ignoreException(defValue = false) {

        GestureDescription.Builder()
                .addStroke(GestureDescription.StrokeDescription(path, 0, time))
                .build()
                .let { dispatchGesture(it, null, null) }
    }

    @CallSuper
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        eventPackageName = event.packageName?.toString()

        //if use postValue. event data is changed. so used setValue
        eventLiveData.value = event
    }
}