package kim.jeonghyeon.sample.viewmodel.startactivity

import android.app.Activity
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel

class StartActivityActivityViewModel(val text: String) : BaseViewModel() {
    fun onSuccess() {
        finish(Activity.RESULT_OK)
    }

    fun onCancel() {
        finish(Activity.RESULT_CANCELED)
    }
}