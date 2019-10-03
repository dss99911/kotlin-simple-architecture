package kim.jeonghyeon.sample

import androidx.fragment.app.viewModels
import kim.jeonghyeon.androidlibrary.architecture.mvvm.MVVMFragment
import kim.jeonghyeon.sample.databinding.FragmentMainBinding
import org.jetbrains.anko.support.v4.toast

/**
 * A placeholder fragment containing a simple view.
 */
class MainFragment : MVVMFragment<MainViewModel, FragmentMainBinding>() {
    override val viewModel: MainViewModel by viewModels()
    override val layoutId: Int
        get() = R.layout.fragment_main

    init {
        setMenu(R.menu.sample_menu) {
            when (it.itemId) {
                R.id.menu_save -> {
                    toast("saved")
                    true
                }
                else -> false
            }
        }
    }
}