package kim.jeonghyeon.androidlibrary.architecture.mvvm

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
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

    internal val addFragment by lazy {
        SingleEventLiveData<RequestFragment>()
    }

    internal val replaceFragment by lazy {
        SingleEventLiveData<RequestFragment>()
    }

    internal val performWithActivity by lazy {
        SingleEventLiveData<(Activity) -> Unit>()
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

    fun addFragment(containerId: Int, fragment: Fragment, tag: String? = null) {
        addFragment.call(RequestFragment(containerId, fragment, tag))
    }

    fun replaceFragment(containerId: Int, fragment: Fragment, tag: String? = null) {
        replaceFragment.call(RequestFragment(containerId, fragment, tag))
    }

    fun performWithActivity(action: (Activity) -> Unit) {
        performWithActivity.call(action)
    }

    data class RequestFragment(val containerId: Int, val fragment: Fragment, val tag: String? = null)
}