package kim.jeonghyeon.androidlibrary.permission

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.SparseArray
import androidx.core.app.ActivityCompat
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

interface IPermissionHandler {
    fun requestPermissions(permissions: Array<String>, listener: PermissionResultListener)
    fun startPermissionSettingsPage(listener: () -> Unit)
    fun checkPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray)
    fun checkActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
}

class PermissionHandler(activity: Activity) : IPermissionHandler {
    private val activityRef: WeakReference<Activity> = WeakReference(activity)
    private val mPermissionResultListeners = SparseArray<PermissionResultListener>()
    private val permissionRequestCode = AtomicInteger(1)
    private var permissionSettingListener: (()->Unit)? = null

    override fun requestPermissions(permissions: Array<String>, listener: PermissionResultListener) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            listener.onPermissionGranted()
            return
        }

        if (permissions.isEmpty()) {
            listener.onPermissionGranted()
            return
        }

        val activity = activityRef.get() ?: return

        val requestCode = permissionRequestCode.getAndIncrement()
        mPermissionResultListeners.put(requestCode, listener)
        ActivityCompat.requestPermissions(activity, permissions, requestCode)
    }

    override fun startPermissionSettingsPage(listener: () -> Unit) {
        val activity = activityRef.get() ?: return

        permissionSettingListener = listener

        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + activity.packageName)).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        activity.startActivityForResult(intent, REQUEST_PERMISSION_SETTING)
    }

    override fun checkPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        val activity = this.activityRef.get() ?: return

        val deniedPermissions = ArrayList<String>()
        var hasPermanentDenied = false
        for (i in permissions.indices) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                continue
            }

            deniedPermissions.add(permissions[i])
            val shouldShow = ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[i])
            if (!shouldShow) {
                hasPermanentDenied = true
            }
        }

        val permissionResultListener = mPermissionResultListeners[requestCode] ?: return
        mPermissionResultListeners.remove(requestCode)

        when {
            deniedPermissions.isEmpty() -> try {
                permissionResultListener.onPermissionGranted()
            } catch (ex: SecurityException) {
                permissionResultListener.onPermissionException()
            }

            hasPermanentDenied -> permissionResultListener.onPermissionDeniedPermanently(deniedPermissions.toTypedArray())
            else -> permissionResultListener.onPermissionDenied(deniedPermissions.toTypedArray())
        }
    }

    override fun checkActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            permissionSettingListener?.invoke()
            permissionSettingListener = null
        }
    }

    companion object {
        private const val REQUEST_PERMISSION_SETTING = 65535//this is max number for request code
    }
}