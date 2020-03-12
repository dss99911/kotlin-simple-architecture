package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseFragment
import kim.jeonghyeon.androidlibrary.architecture.mvvm.bindingViewModel
import kim.jeonghyeon.sample.R

class ViewModelFragment : BaseFragment() {
    override val layoutId: Int = R.layout.fragment_view_model

    val viewModel: ViewModelViewModel by bindingViewModel()

}