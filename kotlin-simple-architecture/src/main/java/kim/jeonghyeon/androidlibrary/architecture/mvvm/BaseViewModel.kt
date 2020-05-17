package kim.jeonghyeon.androidlibrary.architecture.mvvm

import android.annotation.SuppressLint
import android.content.Intent
import androidx.annotation.MainThread
import androidx.annotation.StringRes
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import kim.jeonghyeon.androidlibrary.architecture.livedata.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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
    fun NavDirections.navigate()
    fun navigateUp()
    fun navigateDirection(id: Int)
    fun showSnackbar(text: String)
    fun showSnackbar(@StringRes textId: Int)
    fun showProgressbar()
    fun hideProgressbar()
    fun showOkDialog(message: String, onClick: () -> Unit)

    suspend fun startActivityForResult(intent: Intent): StartActivityResult
    fun requestPermissions(permissions: Array<String>, listener: PermissionResultListener)
    fun startPermissionSettingsPage(listener: () -> Unit)
    fun finish()
    fun finish(resultCode: Int, intent: Intent? = null)

    @MainThread
    fun <T> LiveResource<T>.load(work: suspend CoroutineScope.() -> T)

    operator fun <T> LiveResource<T>.invoke(work: suspend CoroutineScope.() -> T) {
        load(work)
    }

    @MainThread
    fun <T> LiveResource<T>.load(
        work: suspend CoroutineScope.() -> T,
        onResult: (Resource<T>) -> Resource<T>
    )

    @MainThread
    fun <T> LiveResource<T>.load(state: LiveState, work: suspend CoroutineScope.() -> T)

    operator fun <T> LiveResource<T>.invoke(
        state: LiveState,
        work: suspend CoroutineScope.() -> T
    ) {
        load(state, work)
    }

    /**
     * if call several times, and after success, if error occurs, data is not changed even if it's error.
     * use this be carefully.
     */
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

    fun <T> LiveResource(work: suspend CoroutineScope.() -> T): LiveResource<T>
    fun <T> LiveObject(state2: LiveState?, work: suspend CoroutineScope.() -> T): LiveObject<T>

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

open class BaseViewModel(val savedStateHandle: SavedStateHandle? = null) : ViewModel(),
    IBaseViewModel, LifecycleObserver {
    override val state by lazy { LiveState() }
    override val initState by lazy { LiveState() }
    val eventSnackbarByString by lazy { LiveObject<String>() }
    val eventSnackbarById by lazy { LiveObject<Int>() }
    internal val eventStartActivity by lazy { LiveObject<Intent>() }
    val eventStartActivityForResult by lazy { LiveObject<RequestStartActivityResult>() }
    internal val eventRequestPermission by lazy { LiveObject<PermissionData>() }
    internal val eventPermissionSettingPage by lazy { LiveObject<() -> Unit>() }
    internal val eventFinish by lazy { LiveObject<Unit>() }
    internal val eventFinishWithResult by lazy { LiveObject<StartActivityResult>() }


    internal val eventShowProgressBar by lazy { LiveObject<Boolean>() }
    internal val eventShowOkDialog by lazy { LiveObject<OkDialogData>() }

    //this is not shown on inherited viewModel. use function.
    internal val eventNav by lazy { LiveObject<(NavController) -> Unit>() }

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

    override fun NavDirections.navigate() {
        navigateDirection(this)
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

    override suspend fun startActivityForResult(intent: Intent): StartActivityResult =
        suspendCoroutine { continuation ->
            eventStartActivityForResult.call(RequestStartActivityResult(intent) {
                continuation.resume(it)
            })
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

    override fun finish() {
        eventFinish.call()
    }

    override fun finish(resultCode: Int, intent: Intent?) {
        eventFinishWithResult.call(StartActivityResult(resultCode, intent))
    }

    override fun showProgressbar() {
        eventShowProgressBar.call(true)
    }

    override fun hideProgressbar() {
        eventShowProgressBar.call(false)
    }

    override fun showOkDialog(message: String, onClick: () -> Unit) {
        eventShowOkDialog.call(OkDialogData(message, onClick))
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

    override fun <T> LiveResource(work: suspend CoroutineScope.() -> T): LiveResource<T> =
        LiveResource<T>().apply { this(work) }

    override fun <T> LiveObject(
        state2: LiveState?,
        work: suspend CoroutineScope.() -> T
    ): LiveObject<T> = LiveObject(state2, work)

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

data class RequestStartActivityResult(
    val intent: Intent,
    val onResult: (StartActivityResult) -> Unit
)

internal data class PermissionData(
    val permissions: Array<String>,
    val listener: PermissionResultListener
)

internal data class OkDialogData(val message: String, val onClick: () -> Unit)