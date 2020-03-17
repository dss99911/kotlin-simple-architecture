package kim.jeonghyeon.sample.viewmodel.startactivity

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseFragment
import kim.jeonghyeon.androidlibrary.architecture.mvvm.bindingViewModel
import kim.jeonghyeon.sample.R

class StartActivityFragment : BaseFragment() {
    override val layoutId: Int = R.layout.fragment_start_activity

    val viewModel: StartActivityViewModel by bindingViewModel()
}