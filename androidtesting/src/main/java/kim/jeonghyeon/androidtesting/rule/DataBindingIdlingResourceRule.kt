package kim.jeonghyeon.androidtesting.rule

import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.util.*

class DataBindingIdlingResourceRule(
    getFragment: () -> Fragment?
) : TestWatcher() {
    private val idlingResource =
        DataBindingIdlingResource(getFragment)

    override fun starting(description: Description?) {
        IdlingRegistry.getInstance().register(idlingResource)
        super.starting(description)
    }

    override fun finished(description: Description?) {
        IdlingRegistry.getInstance().unregister(idlingResource)
        super.finished(description)
    }

}

class DataBindingIdlingResource(
    private val getFragment: () -> Fragment?
) : IdlingResource {
    // list of registered callbacks
    private val idlingCallbacks = mutableListOf<IdlingResource.ResourceCallback>()
    // give it a unique id to workaround an espresso bug where you cannot register/unregister
    // an idling resource w/ the same name.
    private val id = UUID.randomUUID().toString()
    // holds whether isIdle is called and the result was false. We track this to avoid calling
    // onTransitionToIdle callbacks if Espresso never thought we were idle in the first place.
    private var wasNotIdle = false

    override fun getName() = "DataBinding $id"

    override fun isIdleNow(): Boolean {
        val view = getFragment()?.view ?: return true
        val binding = DataBindingUtil.getBinding<ViewDataBinding>(view) ?: return true
        val idle = !binding.hasPendingBindings()

        @Suppress("LiftReturnOrAssignment")
        if (idle) {
            if (wasNotIdle) {
                // notify observers to avoid espresso race detector
                idlingCallbacks.forEach { it.onTransitionToIdle() }
            }
            wasNotIdle = false
        } else {
            wasNotIdle = true
            // check next frame
            view.postDelayed({
                isIdleNow
            }, 16)
        }
        return idle
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback) {
        idlingCallbacks.add(callback)
    }
}