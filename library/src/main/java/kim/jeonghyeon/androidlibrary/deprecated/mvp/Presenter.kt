package kim.jeonghyeon.androidlibrary.deprecated.mvp

import android.content.Intent
import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.annotation.StringRes
import androidx.fragment.app.FragmentActivity

class Presenter<U : Ui> {
    var ui: U? = null
        private set

    val activity: FragmentActivity?
        get() {
            val ui = ui ?: return null
            return ui.getActivity()
        }

    fun finish() {
        val ui = ui ?: return

        ui.finish()
    }

    @CallSuper
    fun onUiReady(ui: U) {
        this.ui = ui
    }

    fun onStart() {

    }

    fun onResume() {

    }

    fun onPause() {

    }

    fun onStop() {

    }


    @CallSuper
    fun onUiUnready(ui: U) {
        this.ui = null
    }

    fun onSaveInstanceState(outState: Bundle) {}

    fun onRestoreInstanceState(savedInstanceState: Bundle) {}

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {}

    fun getString(@StringRes resourceId: Int): String? {
        if (ui == null) {
            return null
        }
        val context = ui!!.uiContext ?: return null
        return context.getString(resourceId)
    }

    fun getString(@StringRes resourceId: Int, vararg formatArgs: Any): String? {
        if (ui == null) {
            return null
        }
        val context = ui!!.uiContext ?: return null
        return context.getString(resourceId, *formatArgs)
    }

}
