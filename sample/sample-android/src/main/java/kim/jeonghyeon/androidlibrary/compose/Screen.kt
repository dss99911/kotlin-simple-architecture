package kim.jeonghyeon.androidlibrary.compose

import androidx.annotation.CallSuper
import androidx.annotation.FloatRange
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ColumnScope.gravity
import androidx.compose.foundation.layout.RowScope.gravity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import io.ktor.http.*
import kim.jeonghyeon.androidlibrary.R
import kim.jeonghyeon.androidlibrary.compose.widget.ErrorSnackbar
import kim.jeonghyeon.androidlibrary.compose.widget.LoadingBox
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.androidlibrary.extension.toast
import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.type.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
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
 * todo add menu and different navigation icon
 */
abstract class Screen(private vararg val viewModels: BaseViewModel = arrayOf(BaseViewModel())) {

    open val title: String = ""
    open val defaultErrorMessage: String = R.string.error_occurred.resourceToString()

    init {
        //in the Fragment approach, there was event concept that should be called one time for a data.
        //the reason, the event concept is created, is that Fragment or Activity can be recreated,
        //and when observe LiveData, if there is already the data, it observe again, and observer is invoked again.
        //it's Fragment/Activity's limitation.
        //but after migrated to compose, it's not required.
        //now dialog or navigating page all is state. so, no need event to call just one time.
        //just set data. then it'll be reflected
        //and Screen also keep in memory even if activity destroyed. so, no need to handle recreate case.
        //Fragment way was difficult to handle dialog, toast, navigation, so, it couldn't apply MVVM perfectly but event had to use MVP or event implementation on MVVM
        //now complete MVVM is reflected on the architecture, thanks to Jetpack Compose
        viewModels.forEach { viewModel ->
            viewModel.eventGoBack.launchAndCollectNotNull {
                goBack()
            }
            viewModel.eventToast.launchAndCollectNotNull {
                toast(it)
            }
        }
    }

    fun <T> MutableStateFlow<T>.launchAndCollect(onCollect: (T) -> Unit) {
        viewModels[0].scope.launch {
            collect {
                onCollect(it)
            }
        }
    }

    fun <T> MutableStateFlow<T?>.launchAndCollectNotNull(onCollect: (T) -> Unit) {
        launchAndCollect {
            if (it != null) {
                onCollect(it)
            }
        }
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
        viewModels.forEach { it.onCompose() }

        Stack(Modifier.fillMaxSize()) {
            if (composeInitStatus()) {
                return@Stack
            }
            view()
            composeStatus()
        }
    }

    @Composable
    abstract fun view()


    fun onDeeplinkReceived(url: Url) {
        viewModels.forEach { it.onDeeplinkReceived(url) }
    }

    /**
     * LIMITATION : if several viewModel use iniStatus. this won't work properly
     * I recommend to initialize on one viewModel. as it's difficult to show error UI, and retry
     * @return if handled, then return true. handled means no need to compose view()
     */
    @Composable
    private fun StackScope.composeInitStatus(): Boolean {
        viewModels.forEach {
            when (val resource = +it.initStatus) {
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
    private fun StackScope.composeStatus() {
        viewModels.forEach {
            when (val resource = +it.status) {
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
    protected open fun StackScope.composeError(error: Resource.Error<*>) {
        ErrorSnackbar(
            text = error.error.message ?: defaultErrorMessage,
            modifier = Modifier.gravity(Alignment.BottomCenter)
        ) {
            error.retry()
        }
    }

    @Composable
    protected open fun StackScope.composeFullError(error: Resource.Error<*>) {
        ErrorSnackbar(
            text = error.error.message ?: defaultErrorMessage,
            modifier = Modifier.gravity(Alignment.BottomCenter)
        ) {
            error.retry()
        }
    }

    @CallSuper
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

    fun goBack() {
        popUpTo(true)
    }

    /**
     * this just call Modifier functions.
     * the reason to add here same function again,
     * is that we have to add Modifier keyword always and also need to import. importing also takes time.
     */
    protected companion object {
        fun gravity(align: Alignment.Vertical): Modifier = Modifier.gravity(align)
        fun gravity(align: Alignment.Horizontal): Modifier = Modifier.gravity(align)

        fun ColumnScope.weight(@FloatRange(from = 0.0, fromInclusive = false) weight: Float, fill: Boolean = true): Modifier =
            Modifier.weight(weight, fill)
        fun RowScope.weight(@FloatRange(from = 0.0, fromInclusive = false) weight: Float, fill: Boolean = true): Modifier =
            Modifier.weight(weight, fill)

        fun padding(all: Dp) = Modifier.padding(all)

        val Bottom = Alignment.Bottom
        val Top = Alignment.Top
        val CenterHorizontally = Alignment.CenterHorizontally
        val CenterVertically = Alignment.CenterVertically

        inline val Int.dp: Dp get() = Dp(value = this.toFloat())
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
