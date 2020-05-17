package kim.jeonghyeon.sample.viewmodel.navargs

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseFragment
import kim.jeonghyeon.androidlibrary.architecture.mvvm.bindingViewModel
import kim.jeonghyeon.androidlibrary.extension.getNavArgs
import kim.jeonghyeon.sample.R
import org.koin.core.parameter.parametersOf

/**
 * viewModel refer Fragment arguments
 */
class NavArgsFragment : BaseFragment() {

    override val layoutId: Int = R.layout.fragment_nav

    val viewModel: NavArgsViewModel by bindingViewModel {
        parametersOf(getNavArgs<NavArgsFragmentArgs>())
    }

}