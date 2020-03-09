package kim.jeonghyeon.androidtesting

import android.os.Bundle
import android.os.SystemClock
import androidx.annotation.IdRes
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.FragmentScenario
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseFragment
import kim.jeonghyeon.androidtesting.rule.DataBindingIdlingResourceRule
import kim.jeonghyeon.androidtesting.rule.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.Mockito


/**
 * Application is not terminated when change test. so, need to initialize environment with @Before
 */
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
abstract class BaseFragmentTest<F : BaseFragment> : BaseAndroidTest() {
    abstract val theme: Int
    abstract val fragmentClass: Class<F>
    //todo is this good approach? currently fragment is created by each test. and sometimes create activity as well. so, implemention is like this.
    //todo but, consider that BaseFragmentTest create fragment automatically. however, consider how to set data or set arguments before create fragment
    private var fragment: F? = null

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val dataBindingIdlingResourceRule = DataBindingIdlingResourceRule { fragment }

    fun launchFragment(
        fragmentArgs: Bundle = Bundle(), onFragment: (F) -> Unit = {}
    ) = FragmentScenario.launchInContainer(fragmentClass, fragmentArgs, theme, null).onFragment {
        fragment = it
        //sleep for screenshot. if test completed without ui interaction. screenshot is taken before ui drawn.
        SystemClock.sleep(100)
        Navigation.setViewNavController(it.requireView(), Mockito.mock(NavController::class.java))
        onFragment(it)
    }

    fun getFragment() = fragment!!

    inline fun <reified T : NavDirections> assertNavigate(expected: T) {
        val navController = getFragment().findNavController()
        if (!Mockito.mockingDetails(navController).isMock) {
            error("navController should be Mocked")
        }
        val arg = argumentCaptor<NavDirections>()
        Mockito.verify(navController).navigate(
            capture(arg)
        )
        Truth.assertThat(arg.value as T).isEqualTo(expected)
    }

    fun assertNavigate(@IdRes expected: Int) {
        val navController = getFragment().findNavController()
        if (!Mockito.mockingDetails(navController).isMock) {
            error("navController should be Mocked")
        }
        val arg = argumentCaptor<Int>()
        Mockito.verify(navController).navigate(
            capture(arg)
        )
        Truth.assertThat(arg.value).isEqualTo(expected)
    }

    fun assertNeverNavigate() {
        val navController = getFragment().findNavController()
        if (!Mockito.mockingDetails(navController).isMock) {
            error("navController should be Mocked")
        }
        val arg = argumentCaptor<Int>()
        Mockito.verify(navController, Mockito.never()).navigate(
            capture(arg)
        )
    }

    fun assertNavigateUp() {
        val navController = getFragment().findNavController()
        if (!Mockito.mockingDetails(navController).isMock) {
            error("navController should be Mocked")
        }
        Mockito.verify(navController).navigateUp()
    }

}


