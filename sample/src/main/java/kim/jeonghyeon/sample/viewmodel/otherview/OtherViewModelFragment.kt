package kim.jeonghyeon.sample.viewmodel.otherview

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseFragment
import kim.jeonghyeon.androidlibrary.architecture.mvvm.bindingViewModel
import kim.jeonghyeon.sample.MainViewModel
import kim.jeonghyeon.sample.R
import org.koin.core.parameter.parametersOf

class OtherViewModelFragment : BaseFragment() {
    override val layoutId: Int = R.layout.fragment_other_view_model

    val viewModel: OtherViewModelViewModel by bindingViewModel {
        parametersOf(getNavGraphViewModel<MainViewModel>(R.id.mainFragment))
    }
}