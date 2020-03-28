package kim.jeonghyeon.androidlibrary.architecture.mvvm

import android.os.Bundle
import androidx.navigation.fragment.findNavController

/**
 * in case, there is several start destination. use this, then when navigate up, the activity will be  automatically closed.
 */
class EmptyStartDestinationFragment : BaseFragment() {

    override val layoutId: Int = 0

    var startDestinationPassed by savedStateDelegate(false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        findNavController().addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == destination.parent?.startDestination && startDestinationPassed) {
                activity?.finish()
            }

            startDestinationPassed = true
        }
    }
}
