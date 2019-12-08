package kim.jeonghyeon.androidlibrary.permission

import androidx.fragment.app.Fragment

interface IPermissionFragment {
    fun requestPermissions(permissions: Array<String>, listener: PermissionResultListener)
    fun startPermissionSettingsPage(listener: () -> Unit)
}

open class PermissionFragment : Fragment(), IPermissionFragment {
    private val permissionActivity: PermissionActivity?
        get() = activity as? PermissionActivity

    override fun requestPermissions(permissions: Array<String>, listener: PermissionResultListener) {
        permissionActivity?.requestPermissions(permissions, listener)
    }

    override fun startPermissionSettingsPage(listener: () -> Unit) {
        permissionActivity?.startPermissionSettingsPage(listener)
    }
}