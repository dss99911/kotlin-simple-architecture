package kim.jeonghyeon.sample.web

import androidx.fragment.app.viewModels
import kim.jeonghyeon.androidlibrary.architecture.mvvm.MvvmFragment
import kim.jeonghyeon.androidlibrary.databinding.EmptyLayoutBinding
import kim.jeonghyeon.sample.R

/**
 * A placeholder fragment containing a simple view.
 */
class ChromeCustomTabFragment : MvvmFragment<ChromeCustomTabViewModel, EmptyLayoutBinding>() {
    override val viewModel: ChromeCustomTabViewModel by viewModels()

    override val layoutId: Int
        get() = R.layout.empty_layout

}