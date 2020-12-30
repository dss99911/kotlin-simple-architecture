package kim.jeonghyeon.template

import android.os.Bundle
import kim.jeonghyeon.base.HomeViewModel
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.tooling.preview.Preview
import kim.jeonghyeon.androidlibrary.compose.unaryPlus
import kim.jeonghyeon.client.BaseActivity
import kim.jeonghyeon.client.BaseViewModel

class MainActivity : BaseActivity() {
    override val rootViewModel: BaseViewModel = HomeViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setViewModelContent { viewModel ->
            ScreenContent(viewModel)
        }
    }

    //todo remove to library if composable on library is available
    fun setViewModelContent(content: @Composable (model: BaseViewModel) -> Unit) {
        setContent {
            val viewModel = +currentViewModel?:return@setContent
            @Suppress("EXPERIMENTAL_API_USAGE")
            viewModel.onCompose()
            content(viewModel)
        }
    }
}

@Composable
fun ScreenContent(viewModel: BaseViewModel) = when (viewModel) {
    is HomeViewModel -> HomeScreen(viewModel)
    else -> error("Screen is not defined for $viewModel ")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ScreenContent(viewModel = HomeViewModel())
}