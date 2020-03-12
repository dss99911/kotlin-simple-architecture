package kim.jeonghyeon.sample.view

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseFragment
import kim.jeonghyeon.androidlibrary.architecture.mvvm.bindingViewModel
import kim.jeonghyeon.sample.R

class ViewFragment : BaseFragment() {
    override val layoutId: Int = R.layout.fragment_view
    val viewModel: ViewViewModel by bindingViewModel()

}