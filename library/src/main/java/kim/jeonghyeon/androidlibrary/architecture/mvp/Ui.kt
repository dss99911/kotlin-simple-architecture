package kim.jeonghyeon.androidlibrary.architecture.mvp

import android.content.Context

import androidx.fragment.app.FragmentActivity


interface Ui {
    val uiContext: Context?

    val baseActivity: MVPActivity<*, *>?

    fun getActivity(): FragmentActivity?

    fun finish()

    fun requestPermissions(listener: PermissionResultListener, vararg permissions: String)
}