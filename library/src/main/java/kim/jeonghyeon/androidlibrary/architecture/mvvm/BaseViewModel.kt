package kim.jeonghyeon.androidlibrary.architecture.mvvm

import android.app.Activity
import android.content.Intent
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.NavDirections
import kim.jeonghyeon.androidlibrary.architecture.livedata.Resource
import kim.jeonghyeon.androidlibrary.extension.ctx

open class BaseViewModel : ViewModel(), LifecycleObserver {
    val status = MutableLiveData<Resource<Unit>>()

    val toast by lazy {
        EventMutableLiveData<String>()
    }

    val snackbar by lazy {
        EventMutableLiveData<String>()
    }

    val startActivity by lazy {
        EventMutableLiveData<Intent>()
    }

    val startActivityForResult by lazy {
        EventMutableLiveData<Pair<Intent, Int>>()
    }

    val showProgressBar by lazy {
        EventMutableLiveData<Boolean>()
    }

    internal val navDirectionId by lazy {
        EventMutableLiveData<Int>()
    }

    internal val navDirection by lazy {
        EventMutableLiveData<NavDirections>()
    }

    internal val addFragment by lazy {
        EventMutableLiveData<RequestFragment>()
    }

    internal val replaceFragment by lazy {
        EventMutableLiveData<RequestFragment>()
    }

    internal val performWithActivity by lazy {
        EventMutableLiveData<(Activity) -> Unit>()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    open fun onCreate() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    open fun onDestroy() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    open fun onResume() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    open fun onPause() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    open fun onStart() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    open fun onStop() {
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

    fun launchDirection(navDirections: NavDirections) {
        navDirection.call(navDirections)
    }

    fun launchDirection(id: Int) {
        navDirectionId.call(id)
    }

    fun showSnackbar(text: String) {
        snackbar.call(text)
    }

    fun showSnackbar(@StringRes textId: Int) {
        snackbar.call(ctx.getString(textId))
    }

    data class RequestFragment(val containerId: Int, val fragment: Fragment, val tag: String? = null)
}