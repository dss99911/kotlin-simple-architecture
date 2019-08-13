package kim.jeonghyeon.androidlibrary.architecture

import android.content.Intent
import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
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

    /**
     * @param tag to find fragment by tag
     */
    fun addFragment(container: Int, fragment: Fragment, tag: String? = null) {
        val transaction = supportFragmentManager.beginTransaction()
        if (tag == null) {
            transaction.add(container, fragment)
        } else {
            transaction.add(container, fragment, tag)
        }
        transaction.commitNow()
    }

    /**
     * @param tag to find fragment by tag
     */
    fun replaceFragment(container: Int, fragment: Fragment, tag: String? = null) {
        val transaction = supportFragmentManager.beginTransaction()
        if (tag == null) {
            transaction.replace(container, fragment)
        } else {
            transaction.replace(container, fragment, tag)
        }
        transaction.commitNow()
    }
}