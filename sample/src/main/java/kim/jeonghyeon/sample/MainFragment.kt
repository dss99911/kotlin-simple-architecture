package kim.jeonghyeon.sample

import androidx.fragment.app.viewModels
import kim.jeonghyeon.androidlibrary.architecture.mvvm.MvvmFragment
import kim.jeonghyeon.sample.databinding.FragmentMainBinding

/**
 * A placeholder fragment containing a simple view.
 */
class MainFragment : MvvmFragment<MainViewModel, FragmentMainBinding>() {
    override val viewModel: MainViewModel by viewModels()
    override val layoutId: Int
        get() = R.layout.fragment_main

}