package kim.jeonghyeon.sample.feature.apicall

import androidx.fragment.app.viewModels
import kim.jeonghyeon.androidlibrary.architecture.mvvm.MvvmFragment
import kim.jeonghyeon.sample.R
import kim.jeonghyeon.sample.databinding.FragmentApiCallBinding

class ApiCallFragment : MvvmFragment<ApiCallViewModel, FragmentApiCallBinding>() {
    override val viewModel: ApiCallViewModel by viewModels()

    override val layoutId: Int
        get() = R.layout.fragment_api_call

}