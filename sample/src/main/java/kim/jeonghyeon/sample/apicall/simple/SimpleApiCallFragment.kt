package kim.jeonghyeon.sample.apicall.simple

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseFragment
import kim.jeonghyeon.androidlibrary.architecture.mvvm.bindingViewModel
import kim.jeonghyeon.sample.R

class SimpleApiCallFragment : BaseFragment() {
    override val layoutId: Int = R.layout.fragment_simple_api_call

    val viewModel: SimpleApiCallViewModel by bindingViewModel()

}