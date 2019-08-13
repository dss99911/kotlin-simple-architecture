package kim.jeonghyeon.sample.mvvm

import android.view.MenuItem
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel

class SampleViewModel: BaseViewModel() {

    fun onMenuClick(item: MenuItem): Boolean {
        when(item.itemId) {

        }
        return true
    }
}