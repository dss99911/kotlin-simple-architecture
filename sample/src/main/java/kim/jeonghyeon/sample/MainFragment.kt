package kim.jeonghyeon.sample

import android.os.Bundle
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseFragment
import kim.jeonghyeon.androidlibrary.architecture.mvvm.observeEvent
import kim.jeonghyeon.androidlibrary.extension.toast

/**
 * A placeholder fragment containing a simple view.
 */
class MainFragment : BaseFragment() {
    val viewModel by addingViewModel { MainViewModel() }
    override val layoutId: Int
        get() = R.layout.fragment_main

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.clickEvent.observeEvent(this) {
            toast("test")
        }
    }
}