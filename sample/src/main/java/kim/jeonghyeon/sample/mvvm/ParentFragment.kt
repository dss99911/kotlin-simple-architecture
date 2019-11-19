package kim.jeonghyeon.sample.mvvm

import kim.jeonghyeon.androidlibrary.architecture.mvvm.MvvmFragment
import kim.jeonghyeon.androidlibrary.extension.simpleViewModels
import kim.jeonghyeon.sample.R
import kim.jeonghyeon.sample.databinding.FragmentMainBinding

class ParentFragment : MvvmFragment<SampleParentViewModel, FragmentMainBinding>() {

    override val viewModel: SampleParentViewModel by simpleViewModels {
        SampleParentViewModel(getActivityViewModel())
    }
    override val layoutId: Int
        get() = R.layout.fragment_main

}