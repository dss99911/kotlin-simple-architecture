package kim.jeonghyeon.testing

import androidx.annotation.StringRes
import androidx.navigation.NavDirections
import com.google.common.truth.Truth
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel
import kim.jeonghyeon.androidlibrary.extension.ctx
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner

/**
 * whenever change test, application is created, so, koin module's also restarted.
 */
@RunWith(RobolectricTestRunner::class)
@ExperimentalCoroutinesApi
abstract class BaseViewModelTest : BaseRobolectricTest() {

    inline fun <reified T : NavDirections> BaseViewModel.assertNavigateDirection(expected: T) {
        if (!Mockito.mockingDetails(this).isSpy) {
            error("viewModel should be spy")
        }
        val arg = argumentCaptor<NavDirections>()
        Mockito.verify(this).navigateDirection(
            capture(
                arg
            )
        )
        Truth.assertThat(arg.value as T).isEqualTo(expected)
    }

    fun BaseViewModel.assertNavigateUp() {
        if (!Mockito.mockingDetails(this).isSpy) {
            error("viewModel should be spy")
        }
        Mockito.verify(this).navigateUp()
    }

    fun BaseViewModel.assertSnackbar(expected: String) {
        Truth.assertThat(eventSnackbarByString.await()).isEqualTo(expected)
    }

    fun BaseViewModel.assertSnackbar(@StringRes expected: Int) {
        Truth.assertThat(eventSnackbarById.await()).isEqualTo(ctx.getString(expected))
    }
}