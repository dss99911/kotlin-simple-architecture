package kim.jeonghyeon.sample

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseFragment
import kim.jeonghyeon.androidlibrary.architecture.mvvm.bindingViewModel

/**
 * A placeholder fragment containing a simple view.
 */
class MainFragment : BaseFragment() {
    override val layoutId: Int = R.layout.fragment_main

    val viewModel: MainViewModel by bindingViewModel()

}