package com.balancehero.example.androidtesting

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.NavDirections
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@ExperimentalCoroutinesApi
abstract class BaseViewModelTest : KoinTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    inline fun <reified T : NavDirections> BaseViewModel.captureNavigateDirection(): T {
        if (!Mockito.mockingDetails(this).isSpy) {
            error("viewModel should be spy")
        }
        val argTask = argumentCaptor<NavDirections>()
        Mockito.verify(this).navigateDirection(capture(argTask))
        return argTask.value as T
    }
}