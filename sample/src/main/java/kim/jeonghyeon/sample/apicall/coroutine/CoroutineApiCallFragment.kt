package kim.jeonghyeon.sample.apicall.coroutine

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseFragment
import kim.jeonghyeon.androidlibrary.architecture.mvvm.bindingViewModel
import kim.jeonghyeon.sample.R

class CoroutineApiCallFragment : BaseFragment() {
    override val layoutId: Int = R.layout.fragment_coroutine_api_call
    val viewModel: CoroutineApiCallViewModel by bindingViewModel()
}