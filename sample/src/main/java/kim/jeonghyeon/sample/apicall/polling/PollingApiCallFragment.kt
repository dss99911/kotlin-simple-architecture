package kim.jeonghyeon.sample.apicall.polling

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseFragment
import kim.jeonghyeon.androidlibrary.architecture.mvvm.bindingViewModel
import kim.jeonghyeon.sample.R

class PollingApiCallFragment : BaseFragment() {
    override val layoutId: Int = R.layout.fragment_polling_api_call
    val viewModel: PollingApiCallViewModel by bindingViewModel()
}