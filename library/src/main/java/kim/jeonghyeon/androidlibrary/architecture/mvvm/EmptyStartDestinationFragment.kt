package kim.jeonghyeon.androidlibrary.architecture.mvvm

import android.os.Bundle
import androidx.navigation.fragment.findNavController

/**
 * in case, there is several start destination. use this, then when navigate up, the activity will be  automatically closed.
 */
class EmptyStartDestinationFragment : BaseFragment() {

    companion object {
        private const val SAVED_STATE_KEY_START_DESTINATION_PASSED =
            "SAVED_STATE_KEY_START_DESTINATION_PASSED"
    }

    override val layoutId: Int = 0

    private val savedState by savedState()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        findNavController().addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == destination.parent?.startDestination && savedState.get<Boolean>(
                    SAVED_STATE_KEY_START_DESTINATION_PASSED
                ) == true
            ) {
                activity?.finish()
            }

            savedState[SAVED_STATE_KEY_START_DESTINATION_PASSED] = true
        }
    }


}
