package kim.jeonghyeon.androidlibrary.architecture.mvvm

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.NavDestination
import androidx.navigation.fragment.findNavController

/**
 * in case, there is several start destination. use this, then when navigate up, the activity will be  automatically closed.
 */
class EmptyStartDestinationFragment : Fragment() {

    private var currentDestination: NavDestination? = null
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        findNavController().addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == destination.parent?.startDestination && currentDestination != null) {
                activity?.finish()
            }
            currentDestination = destination
        }
    }


}
