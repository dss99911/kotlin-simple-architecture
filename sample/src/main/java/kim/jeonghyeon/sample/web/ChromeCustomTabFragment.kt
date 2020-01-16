package kim.jeonghyeon.sample.web

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseFragment
import kim.jeonghyeon.androidlibrary.architecture.mvvm.bindingViewModel
import kim.jeonghyeon.sample.R

/**
 * A placeholder fragment containing a simple view.
 */
class ChromeCustomTabFragment : BaseFragment() {
    val viewModel: ChromeCustomTabViewModel by bindingViewModel()

    override val layoutId = R.layout.empty_layout

}