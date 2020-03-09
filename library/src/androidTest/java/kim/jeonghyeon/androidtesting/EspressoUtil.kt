package kim.jeonghyeon.androidtesting

import android.text.SpannableString
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Checkable
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.*
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.util.HumanReadables
import kim.jeonghyeon.androidlibrary.extension.ctx
import org.hamcrest.*

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

    fun performClickClickableSpan(@IdRes id: Int, clickableSpan: String) {
        Espresso.onView(ViewMatchers.withId(id)).perform(clickClickableSpan(clickableSpan))
    }

    fun performClickClickableSpan(@IdRes id: Int, @StringRes clickableSpan: Int) {
        Espresso.onView(ViewMatchers.withId(id))
            .perform(clickClickableSpan(ctx.getString(clickableSpan)))
    }

    fun performCheckById(@IdRes id: Int, checked: Boolean) {
        Espresso.onView(ViewMatchers.withId(id)).perform(setChecked(checked))
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

private fun clickClickableSpan(textToClick: CharSequence): ViewAction {
    return object : ViewAction {

        override fun getConstraints(): Matcher<View> {
            return Matchers.instanceOf(TextView::class.java)
        }

        override fun getDescription(): String {
            return "clicking on a ClickableSpan"
        }

        override fun perform(uiController: UiController, view: View) {
            val textView = view as TextView
            val spannableString = textView.text as SpannableString

            if (spannableString.isEmpty()) {
                // TextView is empty, nothing to do
                throw NoMatchingViewException.Builder()
                    .includeViewHierarchy(true)
                    .withRootView(textView)
                    .build()
            }

            // Get the links inside the TextView and check if we find textToClick
            val spans =
                spannableString.getSpans(0, spannableString.length, ClickableSpan::class.java)
            if (spans.isNotEmpty()) {
                var spanCandidate: ClickableSpan
                for (span: ClickableSpan in spans) {
                    spanCandidate = span
                    val start = spannableString.getSpanStart(spanCandidate)
                    val end = spannableString.getSpanEnd(spanCandidate)
                    val sequence = spannableString.subSequence(start, end)
                    if (textToClick.toString().equals(sequence.toString())) {
                        span.onClick(textView)
                        return
                    }
                }
            }

            // textToClick not found in TextView
            throw NoMatchingViewException.Builder()
                .includeViewHierarchy(true)
                .withRootView(textView)
                .build()

        }
    }
}

private fun setChecked(checked: Boolean): ViewAction? {
    return object : ViewAction {
        override fun getConstraints(): BaseMatcher<View> {
            return object : BaseMatcher<View>() {
                override fun matches(item: Any): Boolean {
                    return CoreMatchers.isA(Checkable::class.java).matches(item)
                }

                override fun describeMismatch(item: Any?, mismatchDescription: Description?) {}
                override fun describeTo(description: Description?) {}
            }
        }

        override fun getDescription(): String? {
            return null
        }

        override fun perform(uiController: UiController?, view: View) {
            val checkableView = view as Checkable
            checkableView.isChecked = checked
        }
    }
}

