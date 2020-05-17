package kim.jeonghyeon.sample.view.menu

import android.view.MenuItem
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel
import kim.jeonghyeon.androidlibrary.extension.toast
import kim.jeonghyeon.sample.R

class MenuViewModel : BaseViewModel() {

    fun onMenuClick(item: MenuItem): Boolean {
        toast(item.itemId)
        R.menu.sample_menu
        return true
    }
}