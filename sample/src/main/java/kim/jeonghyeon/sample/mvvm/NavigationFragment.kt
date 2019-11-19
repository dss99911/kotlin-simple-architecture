package kim.jeonghyeon.sample.mvvm

import kim.jeonghyeon.androidlibrary.architecture.mvvm.MvvmFragment
import kim.jeonghyeon.androidlibrary.extension.simpleViewModels
import kim.jeonghyeon.sample.R
import kim.jeonghyeon.sample.databinding.FragmentNavBinding

class NavigationFragment : MvvmFragment<NavigationViewModel, FragmentNavBinding>() {

    override val viewModel: NavigationViewModel by simpleViewModels {
        NavigationViewModel(getNavArgs(), getSavedState())
    }
    override val layoutId: Int
        get() = R.layout.fragment_nav

}