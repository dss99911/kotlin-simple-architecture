package kim.jeonghyeon.androidlibrary.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kim.jeonghyeon.androidlibrary.R
import kim.jeonghyeon.androidlibrary.compose.widget.ErrorSnackbar
import kim.jeonghyeon.androidlibrary.compose.widget.LoadingBox
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.androidlibrary.extension.toast
import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.client.DataFlow
import kim.jeonghyeon.type.Resource
import kim.jeonghyeon.util.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


@Composable
fun Screen(
    viewModel: BaseViewModel,
    initLoading: @Composable () -> Unit = { LoadingView() },
    initErrorView: @Composable BoxScope.(Resource.Error) -> Unit = { ErrorView(it) },
    loading: @Composable () -> Unit = { LoadingView() },
    errorView: @Composable BoxScope.(Resource.Error) -> Unit = { ErrorView(it) },
    children: @Composable (BaseViewModel) -> Unit
) {
    viewModel.toastText.asValue()?.let {
        toast(it)
        viewModel.toastText.setValue(null)
    }

    log.i("Screen $viewModel")

    Box(Modifier.fillMaxSize()) {
        when (val resource = +viewModel.initStatus) {
            is Resource.Loading -> {
                initLoading()
                return@Box
            }

            is Resource.Error -> {
                initErrorView(resource)
                return@Box
            }

            else -> {
            }
        }

        children(viewModel)

        Box(Modifier.fillMaxSize()) {//without Box. after loading, if view() is empty, then shows loading continuously
            when (val resource = +viewModel.status) {
                is Resource.Loading -> loading()
                is Resource.Error -> errorView(resource)
                else -> {
                }
            }
        }
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
    LaunchedTask(this, context) {
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

@Composable
private fun LoadingView() {
    LoadingBox()
}

@Composable
private fun BoxScope.ErrorView(error: Resource.Error) {
    ErrorSnackbar(
        text = error.error().message ?: R.string.error_occurred.resourceToString(),
        modifier = Modifier.align(Alignment.BottomCenter)
    ) {
        error.retry()
    }
}