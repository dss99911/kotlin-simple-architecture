package kim.jeonghyeon.androidlibrary.compose

import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.layout.RowScope.gravity
import androidx.ui.layout.Stack
import kim.jeonghyeon.androidlibrary.R
import kim.jeonghyeon.androidlibrary.compose.widget.ErrorSnackbar
import kim.jeonghyeon.androidlibrary.compose.widget.LoadingBox
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.type.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Todo check if GC is working fine.
 * Todo how to get saved state and viewModel?
 * Todo when screen is changed. even if screen exists on history stack. all coroutine scope job cancelled. it seems not proper.
 *
 *
 * todo when composable is inactive, should the viewModel side process also cancelled? in MVVM. viewModel still alive and also processed. just it's reflected when it's active.
 * todo so, I think that the coroutine scope shouldn't be cancelled.
 * todo so.. use viewModel. and use viewModelScope.
 * todo use savedState also. and viewModel should keep current screen.
 * todo make ScreenScope which survive if it's not in history stack.
 * todo communication between screen.
 * todo multiple first root screen by logic.
 */
abstract class Screen {
    open val title: String = ""

    /**
     * similar with [status] but full page error is shown.
     */
    val initStatus by lazy { statusStateOf() }

    /**
     * screen status of error or loading for event like click
     */
    val status by lazy { statusStateOf() }

    val scope: ScreenScope by lazy { ScreenScope() }

    var isInitialized: AtomicBoolean = AtomicBoolean(false)

    protected open fun initialize() {

    }

    fun clear() {
        scope.close()
    }

    /**
     * !! Limitation !!
     * this function should be overridden in each screen like below
     * @Composable
     * override fun compose() {
     *     super.compose()
     * }
     * todo make plugin which create the code above automatically.
     */
    @Composable
    open fun compose() {
        if (!isInitialized.getAndSet(true)) {
            initialize()
        }

        Stack {
            when (initStatus.value) {
                is Resource.Loading -> {
                    composeFullLoading()
                    return@Stack
                }
                is Resource.Error -> {
                    composeFullError()
                    return@Stack
                }
                else -> {
                }
            }
            view()

            when (status.value) {
                is Resource.Loading -> composeLoading()
                is Resource.Error -> composeError()
                else -> {
                }
            }
        }
    }

    @Composable
    abstract fun view()

    fun <T> ResourceState<T>.load(work: suspend CoroutineScope.() -> T): ResourceState<T> {
        scope.loadResource(this, work)
        return this
    }

    fun <T> ResourceState<T>.load(flow: Flow<Resource<T>>): ResourceState<T> {
        scope.loadFlow(this, null, flow)
        return this
    }

    fun <T> ResourceState<T>.load(status: StatusState, flow: Flow<Resource<T>>): ResourceState<T> {
        scope.loadFlow(this, status, flow)
        return this
    }

    fun <T> ResourceState<T>.load(status: StatusState, work: suspend CoroutineScope.() -> T): ResourceState<T> {
        scope.loadResource(this, status, work)
        return this
    }

    @Composable
    protected open fun composeLoading() {
        LoadingBox()
    }

    @Composable
    protected open fun composeFullLoading() {
        LoadingBox()
    }

    @Composable
    protected open fun composeError() {
        if (status.value.isError()) {
            val error = status.value as Resource.Error
            ErrorSnackbar(text = error.error.message ?: defaultErrorMessage) {
                error.retry()
            }
        }
    }

    @Composable
    protected open fun composeFullError() {
        if (initStatus.value.isError()) {
            val error = initStatus.value as Resource.Error
            ErrorSnackbar(text = error.error.message ?: defaultErrorMessage) {
                error.retry()
            }
        }
    }

    open val defaultErrorMessage: String = R.string.error_occurred.resourceToString()

    protected companion object {
        fun gravity(align: Alignment.Vertical): Modifier = Modifier.gravity(align)

        val Bottom = Alignment.Bottom
        val Top = Alignment.Top
    }
}