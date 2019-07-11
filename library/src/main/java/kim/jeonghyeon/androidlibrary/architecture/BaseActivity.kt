package kim.jeonghyeon.androidlibrary.architecture

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.CallSuper
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import kim.jeonghyeon.androidlibrary.architecture.mvp.PermissionResultListener
import kim.jeonghyeon.androidlibrary.architecture.mvp.PermissionUIHelper
import kim.jeonghyeon.androidlibrary.extension.log

open class BaseActivity : AppCompatActivity() {

    private val permissionUIHelper by lazy {
        PermissionUIHelper(this)
    }

    fun requestPermissions(listener: PermissionResultListener, vararg permissions: String) {
        permissionUIHelper.requestPermissions(permissions, listener)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log("${this::class.simpleName} onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        log("${this::class.simpleName} onDestroy")
    }

    @CallSuper
    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionUIHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun startPermissionSettingsPage(listener: () -> Unit) {
        permissionUIHelper.startPermissionSettingsPage(listener)
    }

    @CallSuper
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        permissionUIHelper.onActivityResult(requestCode, resultCode, data)
    }
}