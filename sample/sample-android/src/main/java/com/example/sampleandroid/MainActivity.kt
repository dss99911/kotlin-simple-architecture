//todo change package name
package com.example.sampleandroid

import android.os.Bundle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.tooling.preview.Preview
import com.example.sampleandroid.ui.AndroidLibraryTheme
import com.example.sampleandroid.view.MainScaffold
import com.example.sampleandroid.view.home.HomeScreen
import com.example.sampleandroid.view.model.*
import kim.jeonghyeon.androidlibrary.compose.asValue
import kim.jeonghyeon.androidlibrary.compose.unaryPlus
import kim.jeonghyeon.client.BaseActivity
import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.client.Deeplink
import kim.jeonghyeon.sample.deeplinkList
import kim.jeonghyeon.sample.viewmodel.*
import kotlinx.coroutines.flow.SharedFlow

class MainActivity : BaseActivity() {
    override val rootViewModel: BaseViewModel = HomeViewModel()

    override val deeplinks: List<Deeplink> = deeplinkList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setViewModelContent { viewModel ->
            AndroidLibraryTheme {
                MainScaffold {
                    Surface(color = MaterialTheme.colors.background) {
                        ScreenContent(viewModel)
                    }
                }
            }
        }
    }

    //todo remove to library if composable on library is available
    fun setViewModelContent(content: @Composable (model: BaseViewModel) -> Unit) {
        setContent {
            currentViewModel.asValue()?.let {
                @Suppress("EXPERIMENTAL_API_USAGE")
                it.onCompose()
                content(it)
            }
        }
    }
}


@Composable
fun ScreenContent(viewModel: BaseViewModel) = when (viewModel) {
    is HomeViewModel -> HomeScreen(viewModel)
    is ApiSingleViewModel -> ApiSingleScreen(viewModel)
    is ApiSingleViewModel2 -> ApiSingleScreen2(viewModel)
    is ApiAnnotationViewModel -> ApiAnnotationScreen(viewModel)
    is ApiAnnotationViewModel2 -> ApiAnnotationScreen2(viewModel)
    is ApiBindingViewModel -> ApiBindingScreen(viewModel)
    is ApiBindingViewModel2 -> ApiBindingScreen2(viewModel)
    is ApiDbViewModel -> ApiDbScreen(viewModel)
    is ApiDbViewModel2 -> ApiDbScreen2(viewModel)
    is ApiExternalViewModel -> ApiExternalScreen(viewModel)
    is ApiExternalViewModel2 -> ApiExternalScreen2(viewModel)
    is ApiHeaderViewModel -> ApiHeaderScreen(viewModel)
    is ApiHeaderViewModel2 -> ApiHeaderScreen2(viewModel)
    is ApiParallelViewModel -> ApiParallelScreen(viewModel)
    is ApiParallelViewModel2 -> ApiParallelScreen2(viewModel)
    is ApiPollingViewModel -> ApiPollingScreen(viewModel)
    is ApiPollingViewModel2 -> ApiPollingScreen2(viewModel)
    is ApiSequentialViewModel -> ApiSequentialScreen(viewModel)
    is ApiSequentialViewModel2 -> ApiSequentialScreen2(viewModel)
    is DbSimpleViewModel -> DbSimpleScreen(viewModel)
    is DbSimpleViewModel2 -> DbSimpleScreen2(viewModel)
    is DeeplinkViewModel -> DeeplinkScreen(viewModel)
    is DeeplinkViewModel2 -> DeeplinkScreen2(viewModel)
    is DeeplinkSubViewModel -> DeeplinkSubScreen(viewModel)
    is DeeplinkSubViewModel2 -> DeeplinkSubScreen2(viewModel)
    is ReactiveViewModel -> ReactiveScreen(viewModel)
    is ReactiveViewModel2 -> ReactiveScreen2(viewModel)
    is SignInViewModel -> SignInScreen(viewModel)
    is SignInViewModel2 -> SignInScreen2(viewModel)
    is SignUpViewModel -> SignUpScreen(viewModel)
    is SignUpViewModel2 -> SignUpScreen2(viewModel)
    is UserViewModel -> UserScreen(viewModel)
    is UserViewModel2 -> UserScreen2(viewModel)
    else -> error("Screen is not defined for $viewModel ")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    HomeScreen(HomeViewModel())
}