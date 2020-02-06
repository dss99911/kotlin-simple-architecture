package kim.jeonghyeon.androidlibrary.architecture.mvvm

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.SparseArray
import androidx.annotation.MainThread
import androidx.annotation.NonNull
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import kim.jeonghyeon.androidlibrary.R
import kim.jeonghyeon.androidlibrary.architecture.livedata.*
import kim.jeonghyeon.androidlibrary.extension.ctx
import kim.jeonghyeon.androidlibrary.extension.log
import kim.jeonghyeon.androidlibrary.extension.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

interface IBaseViewModel {
    val state: LiveState
    val eventSnackbar: LiveObject<String>
    val eventStartActivity: LiveObject<Intent>
    val eventShowProgressBar: LiveObject<Boolean>

    fun onStart()
    fun onResume()
    fun onPause()
    fun onStop()

    fun navigateDirection(navDirections: NavDirections)
    fun navigate(action: (NavController) -> Unit)
    fun navigateDirection(id: Int)
    fun showSnackbar(text: String)
    fun showSnackbar(@StringRes textId: Int)

    fun performWithActivity(action: (BaseActivity) -> Unit)
    fun startActivityForResult(intent: Intent, onResult: (resultCode: Int, data: Intent?) -> Unit)
    fun requestPermissions(permissions: Array<String>, listener: PermissionResultListener)
    fun startPermissionSettingsPage(listener: () -> Unit)

    @MainThread
    fun <T> LiveResource<T>.load(work: suspend CoroutineScope.() -> T): Job

    @MainThread
    fun <T> LiveResource<T>.load(
        work: suspend CoroutineScope.() -> T,
        onResult: (Resource<T>) -> Resource<T>
    ): Job

    @MainThread
    fun <T> LiveResource<T>.load(state: LiveState, work: suspend CoroutineScope.() -> T): Job

    /**
     * if it is loading, ignore
     */
    fun <T> LiveResource<T>.loadOneByOne(work: suspend CoroutineScope.() -> T): Job?

    fun <T, U> LiveResource<U>.loadRetriable(
        part1: suspend CoroutineScope.() -> T,
        part2: suspend CoroutineScope.(T) -> U
    ): Job

    fun <T, U, V> LiveResource<V>.loadRetriable(
        part1: suspend CoroutineScope.() -> T,
        part2: suspend CoroutineScope.(T) -> U,
        part3: suspend CoroutineScope.(U) -> V
    ): Job

    fun <T, U, V, W> LiveResource<W>.loadRetriable(
        part1: suspend CoroutineScope.() -> T,
        part2: suspend CoroutineScope.(T) -> U,
        part3: suspend CoroutineScope.(U) -> V,
        part4: suspend CoroutineScope.(V) -> W
    ): Job
}

open class BaseViewModel : ViewModel(), IBaseViewModel, LifecycleObserver {
    override val state by lazy { LiveState() }
    override val eventSnackbar by lazy { LiveObject<String>() }
    override val eventStartActivity by lazy { LiveObject<Intent>() }

    override val eventShowProgressBar by lazy { LiveObject<Boolean>() }

    //this is not shown on inherited viewModel. use function.
    internal val eventNavDirectionId by lazy { LiveObject<Int>() }
    internal val eventNav by lazy { LiveObject<(NavController) -> Unit>() }
    internal val eventNavDirection by lazy { LiveObject<NavDirections>() }
    @Suppress("DEPRECATION")
    internal val eventPerformWithActivity by lazy { LiveObject<Array<Event<(BaseActivity) -> Unit>>>() }
    private val nextRequestCode by lazy { AtomicInteger(1) }
    private val resultListeners by lazy { SparseArray<(resultCode: Int, data: Intent?) -> Unit>() }
    private val permissionResultListeners by lazy { SparseArray<PermissionResultListener>() }

    init {
        log("initialized")
    }

    override fun onStart() {
    }

    override fun onResume() {
    }

    override fun onPause() {
    }

    override fun onStop() {
    }


    override fun performWithActivity(action: (BaseActivity) -> Unit) {
        val currArray = (eventPerformWithActivity.value ?: emptyArray())
            .filter { event ->
                !event.handled
            }.toTypedArray()

        //this can be used several times
        val nextArray = arrayOf(*currArray, Event(action))
        eventPerformWithActivity.value = nextArray
    }

    override fun navigateDirection(navDirections: NavDirections) {
        eventNavDirection.postValue(navDirections)
    }

    override fun navigateDirection(id: Int) {
        eventNavDirectionId.postValue(id)
    }

    override fun navigate(action: (NavController) -> Unit) {
        eventNav.postValue(action)
    }

    override fun showSnackbar(text: String) {
        eventSnackbar.postValue(text)
    }

    override fun showSnackbar(@StringRes textId: Int) {
        eventSnackbar.postValue(ctx.getString(textId))
    }

    override fun startActivityForResult(
        intent: Intent,
        onResult: (resultCode: Int, data: Intent?) -> Unit
    ) {
        performWithActivity {
            try {
                val viewModel = it.rootViewModel
                val requestCode = viewModel.nextRequestCode.getAndIncrement()
                viewModel.resultListeners.put(requestCode, onResult)
                it.startActivityForResult(intent, requestCode)
            } catch (e: IllegalStateException) {
            } catch (e: ActivityNotFoundException) {
                toast(R.string.toast_no_activity)
            }
        }
    }

    internal fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        resultListeners[requestCode]?.invoke(resultCode, data)
        resultListeners.remove(requestCode)
    }

    @SuppressLint("ObsoleteSdkInt")//this can be used on different minimum sdk
    override fun requestPermissions(
        permissions: Array<String>,
        listener: PermissionResultListener
    ) {
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

            val viewModel = activity.rootViewModel

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

    internal fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
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

    override fun <T> LiveResource<T>.load(work: suspend CoroutineScope.() -> T): Job {
        return viewModelScope.loadResource(this@load, work)
    }

    override fun <T> LiveResource<T>.load(
        work: suspend CoroutineScope.() -> T,
        onResult: (Resource<T>) -> Resource<T>
    ): Job = viewModelScope.loadResource(this@load, work, onResult)

    @MainThread
    override fun <T> LiveResource<T>.load(
        state: LiveState,
        work: suspend CoroutineScope.() -> T
    ): Job =
        viewModelScope.loadResource(this@load, state, work)

    override fun <T> LiveResource<T>.loadOneByOne(work: suspend CoroutineScope.() -> T): Job? {
        if (value.isLoadingNotNull()) {
            return null
        }
        return load(work)
    }

    override fun <T, U> LiveResource<U>.loadRetriable(
        part1: suspend CoroutineScope.() -> T,
        part2: suspend CoroutineScope.(T) -> U
    ): Job = viewModelScope.loadResourcePartialRetryable(this, part1, part2)

    override fun <T, U, V> LiveResource<V>.loadRetriable(
        part1: suspend CoroutineScope.() -> T,
        part2: suspend CoroutineScope.(T) -> U,
        part3: suspend CoroutineScope.(U) -> V
    ): Job = viewModelScope.loadResourcePartialRetryable(this, part1, part2, part3)

    override fun <T, U, V, W> LiveResource<W>.loadRetriable(
        part1: suspend CoroutineScope.() -> T,
        part2: suspend CoroutineScope.(T) -> U,
        part3: suspend CoroutineScope.(U) -> V,
        part4: suspend CoroutineScope.(V) -> W
    ): Job = viewModelScope.loadResourcePartialRetryable(this, part1, part2, part3, part4)
}

inline fun <T> ViewModel.launch(crossinline work: suspend CoroutineScope.() -> T): Job =
    viewModelScope.launch {
        work()
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

/**
 * the reason to use Event instead of SingleLiveEvent is that. SingleLiveEvent is class and difficult to integrate with other livedata
 */
internal class Event<out T>(private val content: T) {

    var handled = false
        private set // Allow external read but not write

    fun handle(): T {
        handled = true
        return content
    }

    /**
     * Returns the content, even if it's already been handled.
     * this is used when one time or multi time both are used.
     */
    fun get(): T = content
}