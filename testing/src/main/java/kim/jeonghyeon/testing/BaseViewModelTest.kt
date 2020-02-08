package kim.jeonghyeon.testing

import androidx.navigation.NavDirections
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel
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

    inline fun <reified T : NavDirections> BaseViewModel.captureNavigateDirection(): T {
        if (!Mockito.mockingDetails(this).isSpy) {
            error("viewModel should be spy")
        }
        val arg = argumentCaptor<NavDirections>()
        Mockito.verify(this).navigateDirection(
            capture(
                arg
            )
        )
        return arg.value as T
    }

    fun BaseViewModel.verifyNavigateUp() {
        if (!Mockito.mockingDetails(this).isSpy) {
            error("viewModel should be spy")
        }
        Mockito.verify(this).navigateUp()
    }
}