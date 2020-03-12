package kim.jeonghyeon.sample.viewmodel.parent

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseFragment
import kim.jeonghyeon.androidlibrary.architecture.mvvm.bindingViewModel
import kim.jeonghyeon.sample.MainActivityViewModel
import kim.jeonghyeon.sample.R
import org.koin.core.parameter.parametersOf

/**
 * viewModel refer the Activity's viewModel
 */
class ParentFragment : BaseFragment() {
    override val layoutId: Int = R.layout.fragment_parent

    val viewModel: ParentViewModel by bindingViewModel {
        parametersOf(getActivityViewModel<MainActivityViewModel>())
    }

}