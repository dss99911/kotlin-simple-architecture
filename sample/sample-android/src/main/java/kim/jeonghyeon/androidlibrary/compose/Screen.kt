package kim.jeonghyeon.androidlibrary.compose

import androidx.annotation.CallSuper
import androidx.annotation.FloatRange
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ColumnScope.gravity
import androidx.compose.foundation.layout.RowScope.gravity
import androidx.compose.runtime.*
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import io.ktor.http.*
import kim.jeonghyeon.androidlibrary.R
import kim.jeonghyeon.androidlibrary.compose.widget.ErrorSnackbar
import kim.jeonghyeon.androidlibrary.compose.widget.LoadingBox
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.androidlibrary.extension.toast
import kim.jeonghyeon.type.Resource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kim.jeonghyeon.androidlibrary.compose.ScreenStack
import kim.jeonghyeon.client.*
import kim.jeonghyeon.util.log
import kotlinx.coroutines.flow.distinctUntilChanged

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
 *
 * multiple viewModel is not supported
 * - it makes code complicated on several functions(go back with result, etc)
 * - only one viewModel is enough(for multiple feature, let viewModel contains sub viewModel)
 */
abstract class Screen(private val viewModel: BaseViewModel = BaseViewModel()) {

    open val title: String = ""
    open val defaultErrorMessage: String = R.string.error_occurred.resourceToString()

    /**
     * this is used for deeplink to root screen.
     * when navigating root screen. all screen is cleared and show root only.
     */
    open val isRoot: Boolean = false

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
        launch {
            viewModel.eventGoBack.collect {
                goBack()
            }
        }
        launch {
            viewModel.eventToast.collect {
                toast(it)
            }
        }
        launch {
            viewModel.eventDeeplink.collect {
                Deeplinker.navigateToDeeplink(this@Screen, it)
            }
        }
    }

    fun launch(block: suspend CoroutineScope.() -> Unit) {
        viewModel.scope.launch {
            block()
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
        viewModel.onCompose()

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

    /**
     * on default way, it delivers deeplink to viewModel
     * you can customize it by override this.
     */
    open fun onDeeplinkReceived(url: Url) {
        viewModel.onDeeplinkReceived(url)
    }

    /**
     * LIMITATION : if several viewModel use iniStatus. this won't work properly
     * I recommend to initialize on one viewModel. as it's difficult to show error UI, and retry
     * @return if handled, then return true. handled means no need to compose view()
     */
    @Composable
    private fun StackScope.composeInitStatus(): Boolean {
        when (val resource = +viewModel.initStatus) {
            is Resource.Loading -> {
                composeInitLoading()
                return true
            }

            is Resource.Error -> {
                composeInitError(resource)
                return true
            }

            else -> {
            }
        }

        return false
    }

    @Composable
    private fun StackScope.composeStatus() {
        Stack {//without Stack. after loading, if view() is empty, then shows loading continuously
            when (val resource = +viewModel.status) {
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
    protected open fun composeInitLoading() {
        LoadingBox()
    }

    @Composable
    protected open fun StackScope.composeError(error: Resource.Error) {
        ErrorSnackbar(
            text = error.error().message ?: defaultErrorMessage,
            modifier = Modifier.gravity(Alignment.BottomCenter)
        ) {
            error.retry()
        }
    }

    @Composable
    protected open fun StackScope.composeInitError(error: Resource.Error) {
        ErrorSnackbar(
            text = error.error().message ?: defaultErrorMessage,
            modifier = Modifier.gravity(Alignment.BottomCenter)
        ) {
            error.retry()
        }
    }

    @CallSuper
    fun clear() {
        viewModel.onBackPressed()
    }

    @Composable
    operator fun <T> DataFlow<T>.unaryPlus(): T? = asState().value

    @Composable
    fun <T> DataFlow<T>.asValue(): T? = asState().value

    @OptIn(ExperimentalCoroutinesApi::class)
    @Composable
    fun <T> DataFlow<T>.asState(
        context: CoroutineContext = Dispatchers.Main
    ): State<T?> = collectDistinctAsState(context)

    fun goBack() {
        popUpTo(true)
    }

    /**
     * if the screen is on the top
     */
    fun isShown() = ScreenStack.last() == this

    /**
     * @param screen the screen to add
     * @param onResult receive result after screen is closed if the screen's viewModel call [BaseViewModel.goBack] with result.
     */
    fun push(screen: Screen, onResult: (ScreenResult) -> Unit) {
        launch {
            screen.viewModel.screenResult.collect {
                onResult(it)
            }
        }
        ScreenStack.instance.add(screen)
    }

    /**
     * this just call Modifier functions.
     * the reason to add here same function again,
     * is that we have to add Modifier keyword always and also need to import. importing also takes time.
     */
    protected companion object {
        fun gravity(align: Alignment.Vertical): Modifier = Modifier.gravity(align)
        fun gravity(align: Alignment.Horizontal): Modifier = Modifier.gravity(align)

        fun ColumnScope.weight(
            @FloatRange(from = 0.0, fromInclusive = false) weight: Float,
            fill: Boolean = true
        ): Modifier =
            Modifier.weight(weight, fill)

        fun RowScope.weight(
            @FloatRange(from = 0.0, fromInclusive = false) weight: Float,
            fill: Boolean = true
        ): Modifier =
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
fun <T> DataFlow<T>.asState(
    context: CoroutineContext = Dispatchers.Main
): State<T?> = collectDistinctAsState(context)

@Composable
fun <T> DataFlow<T>.asValue(): T? = asState().value

@Composable
operator fun <T> DataFlow<T>.unaryPlus(): T? = asValue()


@Composable
fun <T> DataFlow<T>.collectDistinctAsState(
    context: CoroutineContext = EmptyCoroutineContext
): State<T?> {
    val state = remember { mutableStateOf(value) }
    launchInComposition(this, context) {
        if (context == EmptyCoroutineContext) {
            distinctUntilChanged().collect {
                state.value = it
            }
        } else withContext(context) {
            distinctUntilChanged().collect {
                state.value = it
            }
        }
    }
    return state
}

