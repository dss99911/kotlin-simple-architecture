package kim.jeonghyeon.androidlibrary.compose

import androidx.annotation.FloatRange
import androidx.compose.Composable
import androidx.compose.State
import androidx.compose.collectAsState
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.layout.ColumnScope.weight
import androidx.ui.layout.RowScope.gravity
import androidx.ui.layout.Stack
import kim.jeonghyeon.androidlibrary.R
import kim.jeonghyeon.androidlibrary.compose.widget.ErrorSnackbar
import kim.jeonghyeon.androidlibrary.compose.widget.LoadingBox
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.type.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.coroutines.CoroutineContext

/**
 * statusStateOf() shouldn't be lazy. if lazy, it's not working while setValue()
 * WRONG : val a by lazy { stateOf() }
 * CORRECT : val a = stateOf()
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
abstract class Screen(private vararg val viewModels: BaseViewModel = arrayOf(BaseViewModel())) {

    open val title: String = ""
    open val defaultErrorMessage: String = R.string.error_occurred.resourceToString()

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
        viewModels
            .filter { !it.isInitialized.getAndSet(true) }
            .forEach { it.onInitialized() }

        Stack {
            if (composeInitStatus()) {
                return@Stack
            }
            view()
            composeStatus()
        }
    }

    @Composable
    abstract fun view()

    /**
     * LIMITATION : if several viewModel use iniStatus. this won't work properly
     * I recommend to initialize on one viewModel. as it's difficult to show error UI, and retry
     * @return if handled, then return true. handled means no need to compose view()
     */
    @Composable
    private fun composeInitStatus(): Boolean {
        viewModels.forEach {
            when (val resource = it.initStatus.asState().value) {
                is Resource.Loading -> {
                    composeFullLoading()
                    return true
                }

                is Resource.Error -> {
                    composeFullError(resource)
                    return true
                }

                else -> {
                }
            }
        }

        return false
    }

    @Composable
    private fun composeStatus() {
        viewModels.forEach {
            when (val resource = it.status.asState().value) {
                is Resource.Loading -> composeLoading()
                is Resource.Error -> composeError(resource)
                else -> {
                }
            }
        }
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
    protected open fun composeError(error: Resource.Error<*>) {
        ErrorSnackbar(text = error.error.message ?: defaultErrorMessage) {
            error.retry()
        }
    }

    @Composable
    protected open fun composeFullError(error: Resource.Error<*>) {
        ErrorSnackbar(text = error.error.message ?: defaultErrorMessage) {
            error.retry()
        }
    }

    fun clear() {
        viewModels.forEach { it.onCleared() }
    }

    @Composable
    operator fun <T> MutableStateFlow<T>.unaryPlus(): T = asState().value

    @Composable
    fun <T> MutableStateFlow<T>.asValue(): T = asState().value

    @Composable
    inline fun <T> MutableStateFlow<T>.asState(
        context: CoroutineContext = Dispatchers.Main
    ): State<T> = collectAsState(context)

    protected companion object {
        fun gravity(align: Alignment.Vertical): Modifier = Modifier.gravity(align)
        fun weight(@FloatRange(from = 0.0, fromInclusive = false) weight: Float, fill: Boolean = true): Modifier =
            Modifier.weight(weight, fill)

        val Bottom = Alignment.Bottom
        val Top = Alignment.Top
    }

}


@Composable
inline fun <T> MutableStateFlow<T>.asState(
    context: CoroutineContext = Dispatchers.Main
): State<T> = collectAsState(context)

@Composable
fun <T> MutableStateFlow<T>.asValue(): T = asState().value

@Composable
operator fun <T> MutableStateFlow<T>.unaryPlus(): T = asValue()
