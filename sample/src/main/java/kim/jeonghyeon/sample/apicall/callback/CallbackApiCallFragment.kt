package kim.jeonghyeon.sample.apicall.callback

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseFragment
import kim.jeonghyeon.androidlibrary.architecture.mvvm.bindingViewModel
import kim.jeonghyeon.sample.R

class CallbackApiCallFragment : BaseFragment() {
    override val layoutId: Int = R.layout.fragment_callback_api_call
    val viewModel: CallbackApiCallViewModel by bindingViewModel()
}