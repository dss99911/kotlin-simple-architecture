package kim.jeonghyeon.androidlibrary.deprecated.mvp

import android.content.Context

import androidx.fragment.app.FragmentActivity
import kim.jeonghyeon.androidlibrary.permission.PermissionResultListener


interface Ui {
    val uiContext: Context?

    val baseActivity: MVPActivity<*, *>?

    fun getActivity(): FragmentActivity?

    fun finish()

    fun requestPermissions(permissions: Array<String>, listener: PermissionResultListener)
}