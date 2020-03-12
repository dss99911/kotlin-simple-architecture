package kim.jeonghyeon.sample.viewmodel.otherview

import androidx.navigation.navGraphViewModels
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseFragment
import kim.jeonghyeon.androidlibrary.architecture.mvvm.bindingViewModel
import kim.jeonghyeon.sample.MainViewModel
import kim.jeonghyeon.sample.R
import org.koin.core.parameter.parametersOf

class OtherViewModelFragment : BaseFragment() {
    override val layoutId: Int = R.layout.fragment_other_view_model
    //you can call different fragment's viewModel if it's in back stack
    val mainViewModel: MainViewModel by navGraphViewModels(R.id.mainFragment)

    val viewModel: OtherViewModelViewModel by bindingViewModel {
        parametersOf(mainViewModel)
    }


}