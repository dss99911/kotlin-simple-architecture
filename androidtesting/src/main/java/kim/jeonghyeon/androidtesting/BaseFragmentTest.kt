package kim.jeonghyeon.androidtesting

import android.os.Bundle
import android.os.SystemClock
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.fragment.app.testing.FragmentScenario
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.util.HumanReadables
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.runner.RunWith
import org.mockito.Mockito


/**
 * Application is not terminated when change test. so, need to initialize environment with @Before
 */
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
abstract class BaseFragmentTest<F : BaseFragment> : BaseAndroidTest() {
    abstract val theme: Int
    abstract val clazz: Class<F>

    fun launchFragment(
        fragmentArgs: Bundle = Bundle(), onFragment: (F) -> Unit = {}
    ) = FragmentScenario.launchInContainer(clazz, fragmentArgs, theme, null).onFragment {
        //sleep for screenshot. if test completed without ui interaction. screenshot is taken before ui drawn.
        SystemClock.sleep(100)
        Navigation.setViewNavController(it.requireView(), Mockito.mock(NavController::class.java))
        onFragment(it)
    }

    fun launchFragmentWithFragment(
        fragmentArgs: Bundle = Bundle(), onFragment: (F) -> Unit = {}
    ): F {
        lateinit var fragment: F
        launchFragment(fragmentArgs) {
            fragment = it
        }
        return fragment
    }

    fun assertIdDisplayed(@IdRes id: Int) {
        onView(withId(id))
            .check(matches(isDisplayed()))
    }

    fun assertTextDisplayed(@StringRes resId: Int) {
        onView(withText(resId))
            .check(matches(isDisplayed()))
    }

    fun assertTextDisplayed(text: String) {
        onView(withText(text))
            .check(matches(isDisplayed()))
    }

    fun assertTextNotDisplayed(text: String) {
        onView(withText(text)).check(isNotDisplayed())
    }

    fun performClickById(@IdRes id: Int) {
        onView(withId(id)).perform(ViewActions.click())
    }

    fun performClickByText(@StringRes resId: Int) {
        onView(withText(resId)).perform(ViewActions.click())
    }

    fun performClickByText(text: String) {
        onView(withText(text)).perform(ViewActions.click())
    }

    /**
     * even if it's shown, use this for checking recyclerview item.
     * because when use [isDisplayed], view.parent.getChildVisibleRect is false even if the itemview is created and visible
     *
     */
    fun scrollRecyclerView(@IdRes id: Int, position: Int) {
        onView(withId(id))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(position))
    }

    inline fun <reified T : NavDirections> BaseFragment.assertNavigateDirection(expected: T) {
        val navController = findNavController()
        if (!Mockito.mockingDetails(navController).isMock) {
            error("navController should be Mocked")
        }
        val arg = argumentCaptor<NavDirections>()
        Mockito.verify(navController).navigate(
            capture(arg)
        )
        Truth.assertThat(arg.value as T).isEqualTo(expected)
    }
}

fun isNotDisplayed(): ViewAssertion {
    return ViewAssertion { view, noView ->
        if (view != null && isDisplayed().matches(view)) {
            throw AssertionError(
                "View is present in the hierarchy and Displayed: " + HumanReadables.describe(
                    view
                )
            )
        }
    }
}
