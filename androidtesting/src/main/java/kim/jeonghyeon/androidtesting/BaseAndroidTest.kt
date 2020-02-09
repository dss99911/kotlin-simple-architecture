package kim.jeonghyeon.androidtesting

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.graphics.Bitmap
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.util.HumanReadables
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import androidx.test.runner.screenshot.Screenshot
import kim.jeonghyeon.androidtesting.rule.MainCoroutineRule
import kim.jeonghyeon.androidtesting.rule.ScreenshotWatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import java.io.IOException


@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
open class BaseAndroidTest : KoinTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var screenshotRule = RuleChain
        .outerRule(GrantPermissionRule.grant(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE))
        .around(ScreenshotWatcher())

    fun screenshot(name: String) {
        val capture = Screenshot.capture()
        capture.format = Bitmap.CompressFormat.PNG
        capture.name = name
        try {
            capture.process()
        } catch (ex: IOException) {
            throw IllegalStateException(ex)
        }
    }

    fun assertIdDisplayed(@IdRes id: Int) {
        Espresso.onView(ViewMatchers.withId(id))
            .check(ViewAssertions.matches(isDisplayed()))
    }

    fun assertIdNotDisplayed(@IdRes id: Int) {
        Espresso.onView(ViewMatchers.withId(id))
            .check(isNotDisplayed())
    }

    fun assertIdMatchedText(@IdRes id: Int, text: String) {
        Espresso.onView(ViewMatchers.withId(id))
            .check(ViewAssertions.matches(ViewMatchers.withText(text)))
    }

    fun assertTextDisplayed(@StringRes resId: Int) {
        Espresso.onView(ViewMatchers.withText(resId))
            .check(ViewAssertions.matches(isDisplayed()))
    }

    fun assertTextDisplayed(text: String) {
        Espresso.onView(ViewMatchers.withText(text))
            .check(ViewAssertions.matches(isDisplayed()))
    }

    fun assertTextNotDisplayed(text: String) {
        Espresso.onView(ViewMatchers.withText(text)).check(isNotDisplayed())
    }

    fun assertTextNotDisplayed(@IdRes resId: Int) {
        Espresso.onView(ViewMatchers.withText(resId)).check(isNotDisplayed())
    }

    fun assertHintDisplayed(text: String) {
        Espresso.onView(ViewMatchers.withHint(text))
            .check(ViewAssertions.matches(isDisplayed()))
    }

    fun assertIdChecked(@IdRes id: Int) {
        Espresso.onView(ViewMatchers.withId(id))
            .check(ViewAssertions.matches(ViewMatchers.isChecked()))
    }

    fun assertIdNotChecked(@IdRes id: Int) {
        Espresso.onView(ViewMatchers.withId(id))
            .check(ViewAssertions.matches(not(ViewMatchers.isChecked())))
    }

    fun performClickById(@IdRes id: Int) {
        Espresso.onView(ViewMatchers.withId(id)).perform(ViewActions.click())
    }

    fun performClickByText(@StringRes resId: Int) {
        Espresso.onView(ViewMatchers.withText(resId)).perform(ViewActions.click())
    }

    fun performClickByText(text: String) {
        Espresso.onView(ViewMatchers.withText(text)).perform(ViewActions.click())
    }

    fun performTypeText(@IdRes id: Int, text: String) {
        Espresso.onView(ViewMatchers.withId(id))
            .perform(ViewActions.typeText(text), ViewActions.closeSoftKeyboard())
    }

    fun performReplaceText(@IdRes id: Int, text: String) {
        Espresso.onView(ViewMatchers.withId(id))
            .perform(ViewActions.replaceText(text))
    }

    /**
     * even if it's shown, use this for checking recyclerview item.
     * because when use [isDisplayed], view.parent.getChildVisibleRect is false even if the itemview is created and visible
     *
     */
    fun scrollRecyclerView(@IdRes id: Int, position: Int) {
        Espresso.onView(ViewMatchers.withId(id))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(position))
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

