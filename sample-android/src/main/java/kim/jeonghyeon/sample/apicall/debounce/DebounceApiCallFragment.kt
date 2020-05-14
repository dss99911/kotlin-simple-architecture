package kim.jeonghyeon.sample.apicall.debounce

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseFragment
import kim.jeonghyeon.androidlibrary.architecture.mvvm.bindingViewModel
import kim.jeonghyeon.sample.R

class DebounceApiCallFragment : BaseFragment() {
    override val layoutId: Int = R.layout.fragment_debounce_api_call
    val viewModel: DebounceApiCallViewModel by bindingViewModel()
}