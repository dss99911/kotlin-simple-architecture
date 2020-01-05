package kim.jeonghyeon.sample.web

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseFragment
import kim.jeonghyeon.sample.R

/**
 * A placeholder fragment containing a simple view.
 */
class ChromeCustomTabFragment : BaseFragment() {
    val viewModel by addingViewModel { ChromeCustomTabViewModel() }

    override val layoutId = R.layout.empty_layout

}