package kim.jeonghyeon.sample.feature.apicall

import androidx.navigation.navGraphViewModels
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseFragment
import kim.jeonghyeon.androidlibrary.architecture.mvvm.bindingViewModel
import kim.jeonghyeon.sample.MainViewModel
import kim.jeonghyeon.sample.R

class ApiCallFragment : BaseFragment() {
    val viewModel: ApiCallViewModel by bindingViewModel()
    //you can call different fragment's viewModel if it's in back stack
    val mainViewModel: MainViewModel by navGraphViewModels(R.id.mainFragment)

    override val layoutId = R.layout.fragment_api_call

}