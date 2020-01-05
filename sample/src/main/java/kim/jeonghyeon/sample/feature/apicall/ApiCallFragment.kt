package kim.jeonghyeon.sample.feature.apicall

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseFragment
import kim.jeonghyeon.sample.R

class ApiCallFragment : BaseFragment() {
    val viewModel by addingViewModel { ApiCallViewModel() }

    override val layoutId = R.layout.fragment_api_call

}