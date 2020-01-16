package kim.jeonghyeon.sample.mvvm

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseFragment
import kim.jeonghyeon.androidlibrary.architecture.mvvm.bindingViewModel
import kim.jeonghyeon.sample.MainActivityViewModel
import kim.jeonghyeon.sample.R
import org.koin.androidx.viewmodel.ext.android.getSharedViewModel
import org.koin.core.parameter.parametersOf

class ParentFragment : BaseFragment() {

    val viewModel: SampleParentViewModel by bindingViewModel {
        parametersOf(getSharedViewModel<MainActivityViewModel>())
    }
    override val layoutId: Int
        get() = R.layout.fragment_main

}