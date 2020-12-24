package kim.jeonghyeon.androidlibrary.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kim.jeonghyeon.androidlibrary.R
import kim.jeonghyeon.androidlibrary.compose.widget.ErrorSnackbar
import kim.jeonghyeon.androidlibrary.compose.widget.LoadingBox
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.androidlibrary.extension.toast
import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.type.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.coroutines.CoroutineContext


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
        //todo save data on compose side. and don't show toast if it's already shown.
        toast(it)
//        viewModel.toastText.value = null
    }

    Box(Modifier.fillMaxSize()) {
        when (val resource = +viewModel.initStatus) {
            is Resource.Loading -> {
                initLoading()
            }
            is Resource.Error -> {
                initErrorView(resource)
            }
            else -> {
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
    }
}

@Composable
fun <T> Flow<T>.asState(
    context: CoroutineContext = Dispatchers.Main
): State<T?> = collectAsState(
    when (this) {
        is SharedFlow<T> -> {
            replayCache.getOrNull(0)
        }
        is StateFlow<T> -> {
            value
        }
        else -> null
    },
    context
)

@Composable
fun <T> Flow<T>.asValue(): T? = asState().value

@Composable
operator fun <T> Flow<T>.unaryPlus(): T? = asValue()

@Composable
private fun LoadingView() {
    LoadingBox()
}

@Composable
private fun BoxScope.ErrorView(error: Resource.Error) {
    ErrorSnackbar(
        modifier = Modifier.align(Alignment.BottomCenter),
        text = error.error().message ?: R.string.error_occurred.resourceToString()
    ) {
        error.retry()
    }
}