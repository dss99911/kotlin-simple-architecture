package kim.jeonghyeon.sample.view.menu

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseFragment
import kim.jeonghyeon.androidlibrary.architecture.mvvm.bindingViewModel
import kim.jeonghyeon.sample.R

/**
 * shows two menu
 * 1. action bar memu
 * 2. popup menu
 */
class MenuFragment : BaseFragment() {

    override val layoutId: Int = R.layout.fragment_menu

    val viewModel: MenuViewModel by bindingViewModel()

    init {
        setMenu(R.menu.fragment_menu) {
            viewModel.onMenuClick(it)
        }
    }

}