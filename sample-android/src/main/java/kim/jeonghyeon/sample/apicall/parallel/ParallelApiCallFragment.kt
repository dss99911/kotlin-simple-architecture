package kim.jeonghyeon.sample.apicall.parallel

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseFragment
import kim.jeonghyeon.androidlibrary.architecture.mvvm.bindingViewModel
import kim.jeonghyeon.sample.R

class ParallelApiCallFragment : BaseFragment() {
    override val layoutId: Int = R.layout.fragment_parallel_api_call
    val viewModel: ParallelApiCallViewModel by bindingViewModel()
}