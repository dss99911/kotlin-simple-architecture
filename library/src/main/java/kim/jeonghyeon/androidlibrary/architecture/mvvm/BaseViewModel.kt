package kim.jeonghyeon.androidlibrary.architecture.mvvm

import android.app.Activity
import android.content.Intent
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.NavDirections
import kim.jeonghyeon.androidlibrary.architecture.coroutine.loadResource
import kim.jeonghyeon.androidlibrary.architecture.livedata.Resource
import kim.jeonghyeon.androidlibrary.architecture.livedata.ResourceState
import kim.jeonghyeon.androidlibrary.extension.ctx
import kim.jeonghyeon.androidlibrary.permission.PermissionResultListener

interface IBaseViewModel {
    val state: MutableLiveData<ResourceState>
    val eventToast: MutableLiveEvent<String>
    val eventSnackbar: MutableLiveEvent<String>
    val eventStartActivity: MutableLiveEvent<Intent>
    val eventShowProgressBar:MutableLiveEvent<Boolean>

    fun onCreate()
    fun onStart()
    fun onResume()
    fun onPause()
    fun onStop()
    fun onDestroy()

    fun startActivityForResult(intent: Intent, onResult: (resultCode: Int, data: Intent?) -> Unit)
    fun addFragment(containerId: Int, fragment: Fragment, tag: String? = null)
    fun replaceFragment(containerId: Int, fragment: Fragment, tag: String? = null)
    fun performWithActivity(action: (Activity) -> Unit)
    fun navigateDirection(navDirections: NavDirections)
    fun navigateDirection(id: Int)
    fun showSnackbar(text: String)
    fun showSnackbar(@StringRes textId: Int)
    fun requestPermissions(permissions: Array<String>, listener: PermissionResultListener)
    fun startPermissionSettingsPage(listener: () -> Unit)
}

open class BaseViewModel : ViewModel(), IBaseViewModel, LifecycleObserver {
    override val state by lazy { MutableLiveData<Resource<Any>>() }
    override val eventToast by lazy { MutableLiveEvent<String>() }
    override val eventSnackbar by lazy { MutableLiveEvent<String>() }
    override val eventStartActivity by lazy { MutableLiveEvent<Intent>() }

    override val eventShowProgressBar by lazy { MutableLiveEvent<Boolean>() }

    //this is not shown on inherited viewModel. use function.
    internal val eventStartActivityForResult by lazy { MutableLiveEvent<Pair<Intent, (resultCode: Int, data: Intent?) -> Unit>>() }
    internal val eventNavDirectionId by lazy { MutableLiveEvent<Int>() }
    internal val eventNavDirection by lazy { MutableLiveEvent<NavDirections>() }
    internal val eventAddFragment by lazy { MutableLiveEvent<RequestFragment>() }
    internal val eventReplaceFragment by lazy { MutableLiveEvent<RequestFragment>() }
    internal val eventPerformWithActivity by lazy { MutableLiveEvent<(Activity) -> Unit>() }
    internal val eventRequestPermissionEvent by lazy { MutableLiveEvent<RequestPermission>() }
    internal val eventStartPermissionSettingsPageEvent by lazy { MutableLiveEvent<() -> Unit>() }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    override fun onCreate() {
        loadResource(state) {
            return@loadResource "dsf"
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    override fun onStart() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    override fun onResume() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    override fun onPause() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    override fun onStop() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    override fun onDestroy() {
    }

    override fun addFragment(containerId: Int, fragment: Fragment, tag: String?) {
        eventAddFragment.call(RequestFragment(containerId, fragment, tag))
    }

    override fun replaceFragment(containerId: Int, fragment: Fragment, tag: String?) {
        eventReplaceFragment.call(RequestFragment(containerId, fragment, tag))
    }

    override fun performWithActivity(action: (Activity) -> Unit) {
        eventPerformWithActivity.call(action)
    }

    override fun navigateDirection(navDirections: NavDirections) {
        eventNavDirection.call(navDirections)
    }

    override fun navigateDirection(id: Int) {
        eventNavDirectionId.call(id)
    }

    override fun showSnackbar(text: String) {
        eventSnackbar.call(text)
    }

    override fun showSnackbar(@StringRes textId: Int) {
        eventSnackbar.call(ctx.getString(textId))
    }

    override fun requestPermissions(permissions: Array<String>, listener: PermissionResultListener) {
        eventRequestPermissionEvent.call(RequestPermission(permissions, listener))
    }

    override fun startPermissionSettingsPage(listener: () -> Unit) {
        eventStartPermissionSettingsPageEvent.call(listener)
    }

    override fun startActivityForResult(intent: Intent, onResult: (resultCode: Int, data: Intent?) -> Unit) {
        eventStartActivityForResult.call(Pair(intent, onResult))
    }

    internal data class RequestFragment(val containerId: Int, val fragment: Fragment, val tag: String? = null)
    internal class RequestPermission(val permissions: Array<String>, val listener: PermissionResultListener)
}