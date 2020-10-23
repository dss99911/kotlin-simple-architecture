//todo change package name
package com.example.sampleandroid

import android.os.Bundle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.setContent
import androidx.ui.tooling.preview.Preview
import com.example.sampleandroid.ui.AndroidLibraryTheme
import com.example.sampleandroid.view.MainScaffold
import com.example.sampleandroid.view.home.HomeScreen
import com.example.sampleandroid.view.model.*
import kim.jeonghyeon.androidlibrary.compose.Screen
import kim.jeonghyeon.androidlibrary.compose.unaryPlus
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

        setContent {
            AndroidLibraryTheme {
                MainContent {
                    CurrentViewModel {
                        ScreenContent(it)
                    }
                }
            }
        }
    }

    @Composable
    fun CurrentViewModel(screen: @Composable (model: BaseViewModel) -> Unit) {
        val viewModel = +currentViewModel?:return

        //todo is onCompose required?
        @Suppress("EXPERIMENTAL_API_USAGE")
        viewModel.onCompose()

        screen(viewModel)
    }

}

@Composable
fun MainContent(children: @Composable () -> Unit) {
    MainScaffold {
        Surface(color = MaterialTheme.colors.background) {
            children()
        }
        //todo is CrossFade make sub composable to compose several times?

    }
}

//todo Screen can't be used one time. but should be used on each screen.
// it seems because initStatus, status is remembered inside of Screen Composable. so, initStatus, and status remembering should be cancelled to achieve this. not sure for now.
@Composable
fun ScreenContent(viewModel: BaseViewModel) = when (viewModel) {
    is HomeViewModel -> Screen(viewModel) { HomeScreen(viewModel) }
    is ApiSingleViewModel -> Screen(viewModel) { ApiSingleScreen(viewModel)}
    is ApiAnnotationViewModel -> Screen(viewModel) { ApiAnnotationScreen(viewModel)}
    is ApiBindingViewModel -> Screen(viewModel) { ApiBindingScreen(viewModel)}
    is ApiDbViewModel -> Screen(viewModel) { ApiDbScreen(viewModel)}
    is ApiExternalViewModel -> Screen(viewModel) { ApiExternalScreen(viewModel)}
    is ApiHeaderViewModel -> Screen(viewModel) { ApiHeaderScreen(viewModel)}
    is ApiParallelViewModel -> Screen(viewModel) { ApiParallelScreen(viewModel)}
    is ApiPollingViewModel -> Screen(viewModel) { ApiPollingScreen(viewModel)}
    is ApiSequentialViewModel -> Screen(viewModel) { ApiSequentialScreen(viewModel)}
    is DbSimpleViewModel -> Screen(viewModel) { DbSimpleScreen(viewModel)}
    is DeeplinkViewModel -> Screen(viewModel) { DeeplinkScreen(viewModel)}
    is DeeplinkSubViewModel -> Screen(viewModel) { DeeplinkSubScreen(viewModel)}
    is ReactiveViewModel -> Screen(viewModel) { ReactiveScreen(viewModel)}
    is NoReactiveViewModel -> Screen(viewModel) { NoReactiveScreen(viewModel)}
    is SignInViewModel -> Screen(viewModel) { SignInScreen(viewModel)}
    is SignUpViewModel -> Screen(viewModel) { SignUpScreen(viewModel)}
    is UserViewModel -> Screen(viewModel) { UserScreen(viewModel) }
    else -> error("Screen is not defined for $viewModel ")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AndroidLibraryTheme {
        MainContent {
            HomeScreen(HomeViewModel())
        }
    }
}