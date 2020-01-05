package kim.jeonghyeon.androidlibrary.architecture.mvvm

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.SparseArray
import androidx.annotation.NonNull
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import kim.jeonghyeon.androidlibrary.R
import kim.jeonghyeon.androidlibrary.architecture.livedata.ResourceState
import kim.jeonghyeon.androidlibrary.extension.ctx
import kim.jeonghyeon.androidlibrary.extension.toast
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

interface IBaseViewModel {
    val state: LiveData<ResourceState>
    val eventToast: LiveEvent<String>
    val eventSnackbar: LiveEvent<String>
    val eventStartActivity: LiveEvent<Intent>
    val eventShowProgressBar: LiveEvent<Boolean>

    fun onCreate()
    fun onStart()
    fun onResume()
    fun onPause()
    fun onStop()
    fun onDestroy()

    fun addFragment(containerId: Int, fragment: Fragment, tag: String? = null)
    fun replaceFragment(containerId: Int, fragment: Fragment, tag: String? = null)
    fun navigateDirection(navDirections: NavDirections)
    fun navigate(action: (NavController) -> Unit)
    fun navigateDirection(id: Int)
    fun showSnackbar(text: String)
    fun showSnackbar(@StringRes textId: Int)

    fun performWithActivity(action: (BaseActivity) -> Unit)
    fun startActivityForResult(intent: Intent, onResult: (resultCode: Int, data: Intent?) -> Unit)
    fun requestPermissions(permissions: Array<String>, listener: PermissionResultListener)
    fun startPermissionSettingsPage(listener: () -> Unit)
}

open class BaseViewModel : ViewModel(), IBaseViewModel, LifecycleObserver {
    override val state by lazy { MediatorLiveData<ResourceState>() }
    override val eventToast by lazy { MutableLiveEvent<String>() }
    override val eventSnackbar by lazy { MutableLiveEvent<String>() }
    override val eventStartActivity by lazy { MutableLiveEvent<Intent>() }

    override val eventShowProgressBar by lazy { MutableLiveEvent<Boolean>() }

    //this is not shown on inherited viewModel. use function.
    internal val eventNavDirectionId by lazy { MutableLiveEvent<Int>() }
    internal val eventNav by lazy { MutableLiveEvent<(NavController) -> Unit>() }
    internal val eventNavDirection by lazy { MutableLiveEvent<NavDirections>() }
    internal val eventAddFragment by lazy { MutableLiveEvent<RequestFragment>() }
    internal val eventReplaceFragment by lazy { MutableLiveEvent<RequestFragment>() }
    internal val eventPerformWithActivity by lazy { MutableLiveData<Array<Event<(BaseActivity) -> Unit>>>() }
    private val nextRequestCode by lazy { AtomicInteger(1) }
    private val resultListeners by lazy { SparseArray<(resultCode: Int, data: Intent?) -> Unit>() }
    private val permissionResultListeners by lazy { SparseArray<PermissionResultListener>() }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    override fun onCreate() {
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

    override fun performWithActivity(action: (BaseActivity) -> Unit) {
        val currArray = (eventPerformWithActivity.value ?: emptyArray())
            .filter { event ->
                !event.hasBeenHandled
            }.toTypedArray()

        //this can be used several times
        val nextArray = arrayOf(*currArray, Event(action))
        eventPerformWithActivity.value = nextArray
    }

    override fun navigateDirection(navDirections: NavDirections) {
        eventNavDirection.call(navDirections)
    }

    override fun navigateDirection(id: Int) {
        eventNavDirectionId.call(id)
    }

    override fun navigate(action: (NavController) -> Unit) {
        eventNav.call(action)
    }

    override fun showSnackbar(text: String) {
        eventSnackbar.call(text)
    }

    override fun showSnackbar(@StringRes textId: Int) {
        eventSnackbar.call(ctx.getString(textId))
    }

    override fun startActivityForResult(
        intent: Intent,
        onResult: (resultCode: Int, data: Intent?) -> Unit
    ) {
        performWithActivity {
            try {
                val viewModel = it.rootViewModel.value
                val requestCode = viewModel.nextRequestCode.getAndIncrement()
                viewModel.resultListeners.put(requestCode, onResult)
                it.startActivityForResult(intent, requestCode)
            } catch (e: IllegalStateException) {
            } catch (e: ActivityNotFoundException) {
                toast(R.string.toast_no_activity)
            }
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        resultListeners[requestCode]?.invoke(resultCode, data)
        resultListeners.remove(requestCode)
    }

    @SuppressLint("ObsoleteSdkInt")//this can be used on different minimum sdk
    override fun requestPermissions(permissions: Array<String>, listener: PermissionResultListener) {
        //this is called on activity because using requestCode of activity
        performWithActivity { activity ->
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                listener.onPermissionGranted()
                return@performWithActivity
            }

            if (permissions.isEmpty()) {
                listener.onPermissionGranted()
                return@performWithActivity
            }

            val viewModel = activity.rootViewModel.value

            val requestCode = viewModel.nextRequestCode.getAndIncrement()
            viewModel.permissionResultListeners.put(requestCode, listener)
            ActivityCompat.requestPermissions(activity, permissions, requestCode)
        }
    }

    override fun startPermissionSettingsPage(listener: () -> Unit) {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:" + ctx.packageName)
        ).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        startActivityForResult(intent) { _, _ ->
            listener()
        }
    }

    fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        performWithActivity { activity ->
            val deniedPermissions = ArrayList<String>()
            var hasPermanentDenied = false
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    continue
                }

                deniedPermissions.add(permissions[i])
                val shouldShow =
                    ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[i])
                if (!shouldShow) {
                    hasPermanentDenied = true
                }
            }

            val permissionResultListener =
                permissionResultListeners[requestCode] ?: return@performWithActivity
            permissionResultListeners.remove(requestCode)

            when {
                deniedPermissions.isEmpty() -> try {
                    permissionResultListener.onPermissionGranted()
                } catch (ex: SecurityException) {
                    permissionResultListener.onPermissionException()
                }

                hasPermanentDenied -> permissionResultListener.onPermissionDeniedPermanently(
                    deniedPermissions.toTypedArray()
                )
                else -> permissionResultListener.onPermissionDenied(deniedPermissions.toTypedArray())
            }
        }

    }

    internal data class RequestFragment(val containerId: Int, val fragment: Fragment, val tag: String? = null)
}

interface PermissionResultListener {
    /**
     * this will be invoked if all permission granted.
     */
    fun onPermissionGranted() {}

    /**
     * if there is at least one permission is denied, this will be invoked.
     * @param deniedPermissions
     */
    fun onPermissionDenied(deniedPermissions: Array<String>) {}

    /**
     * if there is denied permission and permanently denied permission both.
     * if at least one permanent permission exists, this method is invoked.
     */
    fun onPermissionDeniedPermanently(deniedPermissions: Array<String>) {}

    fun onPermissionException() {}
}