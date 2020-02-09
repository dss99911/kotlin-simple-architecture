package kim.jeonghyeon.sample

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseFragment
import kim.jeonghyeon.androidlibrary.architecture.mvvm.bindingViewModel

/**
 * A placeholder fragment containing a simple view.
 */
class MainFragment : BaseFragment() {
    val viewModel: MainViewModel by bindingViewModel()
    override val layoutId: Int
        get() = R.layout.fragment_main

}