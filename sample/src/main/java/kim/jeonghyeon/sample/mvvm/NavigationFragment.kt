package kim.jeonghyeon.sample.mvvm

import kim.jeonghyeon.androidlibrary.architecture.mvvm.MvvmFragment
import kim.jeonghyeon.androidlibrary.extension.simpleViewModels
import kim.jeonghyeon.sample.R
import kim.jeonghyeon.sample.databinding.FragmentMainBinding

class NavigationFragment : MvvmFragment<NavigationViewModel, FragmentMainBinding>() {

    override val viewModel: NavigationViewModel by simpleViewModels()
    override val layoutId: Int
        get() = R.layout.fragment_nav

}