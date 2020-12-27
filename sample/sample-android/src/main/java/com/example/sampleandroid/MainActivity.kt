//todo change package name
package com.example.sampleandroid

import android.os.Bundle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.tooling.preview.Preview
import com.example.sampleandroid.ui.AndroidLibraryTheme
import com.example.sampleandroid.view.MainScaffold
import com.example.sampleandroid.view.home.HomeScreen
import com.example.sampleandroid.view.model.*
import kim.jeonghyeon.androidlibrary.compose.asValue
import kim.jeonghyeon.client.BaseActivity
import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.client.Deeplink
import kim.jeonghyeon.sample.deeplinkList
import kim.jeonghyeon.sample.viewmodel.*

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
    is ApiAnnotationViewModel -> ApiAnnotationScreen(viewModel)
    is ApiBindingViewModel -> ApiBindingScreen(viewModel)
    is ApiDbViewModel -> ApiDbScreen(viewModel)
    is ApiExternalViewModel -> ApiExternalScreen(viewModel)
    is ApiHeaderViewModel -> ApiHeaderScreen(viewModel)
    is ApiParallelViewModel -> ApiParallelScreen(viewModel)
    is ApiPollingViewModel -> ApiPollingScreen(viewModel)
    is ApiSequentialViewModel -> ApiSequentialScreen(viewModel)
    is DbSimpleViewModel -> DbSimpleScreen(viewModel)
    is DeeplinkViewModel -> DeeplinkScreen(viewModel)
    is DeeplinkSubViewModel -> DeeplinkSubScreen(viewModel)
    is SearchViewModel -> SearchScreen(viewModel)
    is SignInViewModel -> SignInScreen(viewModel)
    is SignUpViewModel -> SignUpScreen(viewModel)
    is UserViewModel -> UserScreen(viewModel)
    is RetrofitViewModel -> RetrofitScreen(viewModel)
    else -> error("Screen is not defined for $viewModel ")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    HomeScreen(HomeViewModel())
}