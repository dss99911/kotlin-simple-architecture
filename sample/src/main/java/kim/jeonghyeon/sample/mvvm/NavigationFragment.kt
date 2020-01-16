package kim.jeonghyeon.sample.mvvm

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseFragment
import kim.jeonghyeon.androidlibrary.architecture.mvvm.bindingViewModel
import kim.jeonghyeon.sample.R
import org.koin.core.parameter.parametersOf

class NavigationFragment : BaseFragment() {

    val viewModel: NavigationViewModel by bindingViewModel {
        parametersOf(getNavArgs<NavigationFragmentArgs>(), getSavedState())
    }
    override val layoutId: Int
        get() = R.layout.fragment_nav

}