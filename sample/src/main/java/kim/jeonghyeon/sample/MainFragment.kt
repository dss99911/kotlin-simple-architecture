package kim.jeonghyeon.sample

import kim.jeonghyeon.androidlibrary.architecture.mvvm.MVVMFragment
import kim.jeonghyeon.androidlibrary.extension.getViewModel
import kim.jeonghyeon.sample.databinding.FragmentMainBinding

/**
 * A placeholder fragment containing a simple view.
 */
class MainFragment : MVVMFragment<MainViewModel, FragmentMainBinding>() {
    override val viewModel: MainViewModel
        get() = getViewModel()
    override val layoutId: Int
        get() = R.layout.fragment_main


}