package kim.jeonghyeon.sample.feature.apicall

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseFragment
import kim.jeonghyeon.androidlibrary.architecture.mvvm.bindingViewModel
import kim.jeonghyeon.sample.R

class ApiCallFragment : BaseFragment() {
    val viewModel: ApiCallViewModel by bindingViewModel()

    override val layoutId = R.layout.fragment_api_call

}