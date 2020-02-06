package com.balancehero.example.androidtesting

import android.os.Bundle
import android.os.SystemClock
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.FragmentScenario
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.runner.RunWith


/**
 * Application is not terminated when change test. so, need to initialize environment with @Before
 */
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
abstract class BaseFragmentTest<F : Fragment> : BaseAndroidTest() {
    abstract val theme: Int
    abstract val clazz: Class<F>

    fun launchFragment(
        fragmentArgs: Bundle = Bundle(), onFragment: (F) -> Unit = {}
    ) = FragmentScenario.launchInContainer(clazz, fragmentArgs, theme, null).onFragment {
        SystemClock.sleep(100)
        onFragment(it)
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
        onView(withText(text)).check(doesNotExist())
    }

    fun performClickWithId(@IdRes id: Int) {
        onView(withId(id)).perform(ViewActions.click())
    }


    fun performClickWithText(@StringRes resId: Int) {
        onView(withText(resId)).perform(ViewActions.click())
    }

    fun performClickWithText(text: String) {
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
}

