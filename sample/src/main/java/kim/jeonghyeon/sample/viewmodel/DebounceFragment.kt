package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseFragment
import kim.jeonghyeon.androidlibrary.architecture.mvvm.bindingViewModel
import kim.jeonghyeon.sample.R

class DebounceFragment : BaseFragment() {
    override val layoutId: Int
        get() = R.layout.fragment_debounce

    val viewModel by bindingViewModel<DebounceViewModel>()
}