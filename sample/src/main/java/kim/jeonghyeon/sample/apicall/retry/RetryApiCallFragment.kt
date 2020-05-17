package kim.jeonghyeon.sample.apicall.retry

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseFragment
import kim.jeonghyeon.androidlibrary.architecture.mvvm.bindingViewModel
import kim.jeonghyeon.sample.R

class RetryApiCallFragment : BaseFragment() {
    override val layoutId: Int = R.layout.fragment_retry_api_call
    val viewModel: RetryApiCallViewModel by bindingViewModel()
}