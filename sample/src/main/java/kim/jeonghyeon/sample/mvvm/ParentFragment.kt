package kim.jeonghyeon.sample.mvvm

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseFragment
import kim.jeonghyeon.sample.R

class ParentFragment : BaseFragment() {

    val viewModel: SampleParentViewModel by addingViewModel {
        SampleParentViewModel(getActivityViewModel())
    }
    override val layoutId: Int
        get() = R.layout.fragment_main

}