package kim.jeonghyeon.androidlibrary.permission

import android.content.Intent
import androidx.annotation.CallSuper
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity

interface IPermissionActivity {
    fun requestPermissions(permissions: Array<String>, listener: PermissionResultListener)
    fun startPermissionSettingsPage(listener: () -> Unit)
}

open class PermissionActivity : AppCompatActivity(), IPermissionActivity {
    private val permissionHandler by lazy {
        PermissionHandler(this)
    }

    override fun requestPermissions(permissions: Array<String>, listener: PermissionResultListener) {
        permissionHandler.requestPermissions(permissions, listener)
    }

    override fun startPermissionSettingsPage(listener: () -> Unit) {
        permissionHandler.startPermissionSettingsPage(listener)
    }

    @CallSuper
    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionHandler.checkPermissionsResult(requestCode, permissions, grantResults)
    }

    @CallSuper
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        permissionHandler.checkActivityResult(requestCode, resultCode, data)
    }
}