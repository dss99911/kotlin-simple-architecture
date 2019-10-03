package kim.jeonghyeon.sample.ui

import kim.jeonghyeon.androidlibrary.architecture.mvvm.MVVMFragment
import kim.jeonghyeon.androidlibrary.extension.argumentViewModels
import kim.jeonghyeon.sample.R
import kim.jeonghyeon.sample.databinding.FragmentMainBinding

class NavigationFragment : MVVMFragment<NavigationViewModel, FragmentMainBinding>() {

    override val viewModel: NavigationViewModel by argumentViewModels()
    override val layoutId: Int
        get() = R.layout.fragment_main

}