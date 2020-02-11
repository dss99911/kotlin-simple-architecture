package kim.jeonghyeon.androidtesting

import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.util.HumanReadables
import org.hamcrest.Matchers

interface EspressoUtil {
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
            .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isChecked())))
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

private fun isNotDisplayed(): ViewAssertion {
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

