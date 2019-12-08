package kim.jeonghyeon.sample.feature.apicall

import androidx.fragment.app.viewModels
import kim.jeonghyeon.androidlibrary.architecture.mvvm.MvvmFragment
import kim.jeonghyeon.androidlibrary.databinding.EmptyLayoutBinding
import kim.jeonghyeon.sample.R

class ApiCallFragment : MvvmFragment<ApiCallViewModel, EmptyLayoutBinding>() {
    override val viewModel: ApiCallViewModel by viewModels()

    override val layoutId: Int
        get() = R.layout.empty_layout

}