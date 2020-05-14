package kim.jeonghyeon.sample.apicall.reactive

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseFragment
import kim.jeonghyeon.androidlibrary.architecture.mvvm.bindingViewModel
import kim.jeonghyeon.sample.R

class ReactiveApiCallFragment : BaseFragment() {
    override val layoutId: Int = R.layout.fragment_reactive_api_call
    val viewModel: ReactiveApiCallViewModel by bindingViewModel()
}