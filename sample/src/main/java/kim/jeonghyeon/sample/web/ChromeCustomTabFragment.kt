package kim.jeonghyeon.sample.web

import kim.jeonghyeon.androidlibrary.architecture.mvvm.MVVMFragment
import kim.jeonghyeon.androidlibrary.databinding.EmptyLayoutBinding
import kim.jeonghyeon.androidlibrary.extension.getViewModel

/**
 * A placeholder fragment containing a simple view.
 */
class ChromeCustomTabFragment : MVVMFragment<ChromeCustomTabViewModel, EmptyLayoutBinding>() {
    override val viewModel: ChromeCustomTabViewModel
        get() = getViewModel()

    override val layoutId: Int
        get() = kim.jeonghyeon.androidlibrary.R.layout.empty_layout

}