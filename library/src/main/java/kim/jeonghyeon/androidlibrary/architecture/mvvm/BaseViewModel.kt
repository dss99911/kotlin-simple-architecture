package kim.jeonghyeon.androidlibrary.architecture.mvvm

import android.annotation.SuppressLint
import android.content.Intent
import androidx.annotation.MainThread
import androidx.annotation.StringRes
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import kim.jeonghyeon.androidlibrary.architecture.livedata.*
import kim.jeonghyeon.androidlibrary.extension.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

interface IBaseViewModel {
    /**
     * normally used for user action. and not hide page
     */
    val state: LiveState
    /**
     * hide if it's not success
     * normally used for page init.
     * but if need to hide page. you can use this
     */
    val initState: LiveState

    fun onStart()
    fun onResume()
    fun onPause()
    fun onStop()

    fun navigateDirection(navDirections: NavDirections)
    fun navigateUp()
    fun navigateDirection(id: Int)
    fun showSnackbar(text: String)
    fun showSnackbar(@StringRes textId: Int)
    fun showProgressbar()
    fun hideProgressbar()

    fun startActivityForResult(intent: Intent, onResult: (resultCode: Int, data: Intent?) -> Unit)
    fun requestPermissions(permissions: Array<String>, listener: PermissionResultListener)
    fun startPermissionSettingsPage(listener: () -> Unit)

    @MainThread
    fun <T> LiveResource<T>.load(work: suspend CoroutineScope.() -> T)

    @MainThread
    fun <T> LiveResource<T>.load(
        work: suspend CoroutineScope.() -> T,
        onResult: (Resource<T>) -> Resource<T>
    )

    @MainThread
    fun <T> LiveResource<T>.load(state: LiveState, work: suspend CoroutineScope.() -> T)

    @MainThread
    fun <T> LiveObject<T>.loadDataAndState(
        state2: LiveState?,
        work: suspend CoroutineScope.() -> T
    ): Job

    /**
     * if it is loading, ignore
     */
    fun <T> LiveResource<T>.loadInIdle(work: suspend CoroutineScope.() -> T)

    fun <T> loadInIdle(work: suspend CoroutineScope.() -> T)

    fun <T> LiveResource<T>.loadDebounce(timeInMillis: Long, work: suspend CoroutineScope.() -> T)

    //TODO HYUN : this has learning curve which is not straight-forward to understand. consider to delete
    fun <T, U> LiveResource<U>.loadRetriable(
        part1: suspend CoroutineScope.() -> T,
        part2: suspend CoroutineScope.(T) -> U
    )

    fun <T, U, V> LiveResource<V>.loadRetriable(
        part1: suspend CoroutineScope.() -> T,
        part2: suspend CoroutineScope.(T) -> U,
        part3: suspend CoroutineScope.(U) -> V
    )

    fun <T, U, V, W> LiveResource<W>.loadRetriable(
        part1: suspend CoroutineScope.() -> T,
        part2: suspend CoroutineScope.(T) -> U,
        part3: suspend CoroutineScope.(U) -> V,
        part4: suspend CoroutineScope.(V) -> W
    )
}

open class BaseViewModel : ViewModel(), IBaseViewModel, LifecycleObserver {
    override val state by lazy { LiveState() }
    override val initState by lazy { LiveState() }
    internal val eventSnackbarByString by lazy { LiveObject<String>() }
    internal val eventSnackbarById by lazy { LiveObject<Int>() }
    internal val eventStartActivity by lazy { LiveObject<Intent>() }
    internal val eventStartActivityForResult by lazy { LiveObject<StartActivityResultData>() }
    internal val eventRequestPermission by lazy { LiveObject<PermissionData>() }
    internal val eventPermissionSettingPage by lazy { LiveObject<() -> Unit>() }

    internal val eventShowProgressBar by lazy { LiveObject<Boolean>() }

    //this is not shown on inherited viewModel. use function.
    internal val eventNav by lazy { LiveObject<(NavController) -> Unit>() }

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

    override fun navigateDirection(navDirections: NavDirections) {
        navigate { it.navigate(navDirections) }
    }

    override fun navigateDirection(id: Int) {
        navigate { it.navigate(id) }
    }

    override fun navigateUp() {
        navigate { it.navigateUp() }
    }

    private fun navigate(action: (NavController) -> Unit) {
        eventNav.postValue(action)
    }

    override fun showSnackbar(text: String) {
        eventSnackbarByString.call(text)
    }

    override fun showSnackbar(@StringRes textId: Int) {
        eventSnackbarById.call(textId)
    }

    override fun startActivityForResult(
        intent: Intent,
        onResult: (resultCode: Int, data: Intent?) -> Unit
    ) {
        eventStartActivityForResult.call(StartActivityResultData(intent, onResult))
    }

    @SuppressLint("ObsoleteSdkInt")//this can be used on different minimum sdk
    override fun requestPermissions(
        permissions: Array<String>,
        listener: PermissionResultListener
    ) {
        eventRequestPermission.call(PermissionData(permissions, listener))
    }

    override fun startPermissionSettingsPage(listener: () -> Unit) {
        eventPermissionSettingPage.call(listener)
    }

    override fun showProgressbar() {
        eventShowProgressBar.call(true)
    }

    override fun hideProgressbar() {
        eventShowProgressBar.call(false)
    }

    override fun <T> LiveResource<T>.load(work: suspend CoroutineScope.() -> T) {
        viewModelScope.loadResource(this@load, work)
    }

    override fun <T> LiveResource<T>.load(
        work: suspend CoroutineScope.() -> T,
        onResult: (Resource<T>) -> Resource<T>
    ) {
        viewModelScope.loadResource(this@load, work, onResult)
    }

    @MainThread
    override fun <T> LiveResource<T>.load(
        state: LiveState,
        work: suspend CoroutineScope.() -> T
    ) {

        viewModelScope.loadResource(this@load, state, work)
    }

    @MainThread
    override fun <T> LiveObject<T>.loadDataAndState(
        state2: LiveState?,
        work: suspend CoroutineScope.() -> T
    ): Job =
        viewModelScope.loadDataAndState(this@loadDataAndState, state2, work)

    override fun <T> LiveResource<T>.loadInIdle(work: suspend CoroutineScope.() -> T) {
        if (value.isLoadingState()) {
            return
        }
        load(work)
    }

    override fun <T> loadInIdle(work: suspend CoroutineScope.() -> T) {
        state.loadInIdle(work)
    }

    override fun <T> LiveResource<T>.loadDebounce(
        timeInMillis: Long,
        work: suspend CoroutineScope.() -> T
    ) {
        value?.onLoading { it?.cancel() }
        load {
            delay(timeInMillis)
            work()
        }
    }

    override fun <T, U> LiveResource<U>.loadRetriable(
        part1: suspend CoroutineScope.() -> T,
        part2: suspend CoroutineScope.(T) -> U
    ) {
        viewModelScope.loadResourcePartialRetryable(this, part1, part2)
    }

    override fun <T, U, V> LiveResource<V>.loadRetriable(
        part1: suspend CoroutineScope.() -> T,
        part2: suspend CoroutineScope.(T) -> U,
        part3: suspend CoroutineScope.(U) -> V
    ) {
        viewModelScope.loadResourcePartialRetryable(this, part1, part2, part3)
    }

    override fun <T, U, V, W> LiveResource<W>.loadRetriable(
        part1: suspend CoroutineScope.() -> T,
        part2: suspend CoroutineScope.(T) -> U,
        part3: suspend CoroutineScope.(U) -> V,
        part4: suspend CoroutineScope.(V) -> W
    ) {
        viewModelScope.loadResourcePartialRetryable(this, part1, part2, part3, part4)
    }
}

internal data class StartActivityResultData(
    val intent: Intent,
    val onResult: (resultCode: Int, data: Intent?) -> Unit
)

internal data class PermissionData(
    val permissions: Array<String>,
    val listener: PermissionResultListener
)