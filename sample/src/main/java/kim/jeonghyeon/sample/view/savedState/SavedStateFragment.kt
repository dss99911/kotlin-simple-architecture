package kim.jeonghyeon.sample.view.savedState

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseFragment
import kim.jeonghyeon.androidlibrary.architecture.mvvm.bindingViewModel
import kim.jeonghyeon.androidlibrary.extension.toast
import kim.jeonghyeon.sample.R
import org.koin.core.parameter.parametersOf

/**
 * The way to test
 * 1. run on android version 19(I'm not sure from which version. but latest android version can't use 'ps' command)
 * 2. go to this fragment
 * 3. go to home(the fragment becomes background status)
 * 4. adb shell ps | grep 'kim.jeonghyeon.sample'
 * 5. kill {pid}
 * 6. open the app from recent app list
 */
class SavedStateFragment : BaseFragment() {
    override val layoutId: Int = R.layout.fragment_saved_state

    val viewModel: SavedStateViewModel by bindingViewModel {
        parametersOf(savedState)
    }

    override fun onViewModelSetup() {
        viewModel.liveData.observe {
            toast("data1 changed : $it")
        }

        viewModel.data2.observe {
            toast("data2 changed : $it")
        }
    }
}