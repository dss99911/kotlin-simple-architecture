package kim.jeonghyeon.androidlibrary.architecture.mvvm

import android.content.Intent
import androidx.lifecycle.ViewModel

open class BaseViewModel : ViewModel() {
    val toast by lazy {
        SingleEventLiveData<String?>()
    }

    val startActivity by lazy {
        SingleEventLiveData<Intent>()
    }

    val startActivityForResult by lazy {
        SingleEventLiveData<Pair<Intent, Int>>()
    }

    val showProgressBar by lazy {
        SingleEventLiveData<Boolean>()
    }

    open fun onCreate() {

    }

    open fun onStart() {

    }
    open fun onResume() {

    }
    open fun onPause() {

    }
    open fun onStop() {

    }
    open fun onDestroy() {

    }

    open fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

    }
}