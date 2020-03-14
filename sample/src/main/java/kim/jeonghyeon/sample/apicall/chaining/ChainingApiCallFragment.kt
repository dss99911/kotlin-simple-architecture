package kim.jeonghyeon.sample.apicall.chaining

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseFragment
import kim.jeonghyeon.androidlibrary.architecture.mvvm.bindingViewModel
import kim.jeonghyeon.sample.R

class ChainingApiCallFragment : BaseFragment() {
    override val layoutId: Int = R.layout.fragment_chaining_api_call
    val viewModel: ChainingApiCallViewModel by bindingViewModel()
}