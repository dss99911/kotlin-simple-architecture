package kim.jeonghyeon.sample.mvvm

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseFragment
import kim.jeonghyeon.sample.R

class NavigationFragment : BaseFragment() {

    val viewModel: NavigationViewModel by addingViewModel {
        NavigationViewModel(getNavArgs(), getSavedState())
    }
    override val layoutId: Int
        get() = R.layout.fragment_nav

}