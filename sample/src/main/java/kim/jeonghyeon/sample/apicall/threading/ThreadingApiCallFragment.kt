package kim.jeonghyeon.sample.apicall.threading

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseFragment
import kim.jeonghyeon.androidlibrary.architecture.mvvm.bindingViewModel
import kim.jeonghyeon.sample.R

class ThreadingApiCallFragment : BaseFragment() {
    override val layoutId: Int = R.layout.fragment_threading_api_call
    val viewModel: ThreadingApiCallViewModel by bindingViewModel()
}