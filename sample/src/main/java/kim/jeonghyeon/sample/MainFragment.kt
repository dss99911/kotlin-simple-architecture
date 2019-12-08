package kim.jeonghyeon.sample

import android.os.Bundle
import androidx.fragment.app.viewModels
import kim.jeonghyeon.androidlibrary.architecture.mvvm.MvvmFragment
import kim.jeonghyeon.androidlibrary.architecture.mvvm.observeEvent
import kim.jeonghyeon.sample.databinding.FragmentMainBinding
import org.jetbrains.anko.support.v4.toast

/**
 * A placeholder fragment containing a simple view.
 */
class MainFragment : MvvmFragment<MainViewModel, FragmentMainBinding>() {
    override val viewModel: MainViewModel by viewModels()
    override val layoutId: Int
        get() = R.layout.fragment_main

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.clickEvent.observeEvent(this) {
            toast("test")
        }
    }
}