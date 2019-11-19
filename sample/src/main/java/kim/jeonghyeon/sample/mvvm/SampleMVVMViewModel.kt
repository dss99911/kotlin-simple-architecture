package kim.jeonghyeon.sample.mvvm

import android.view.MenuItem
import androidx.lifecycle.SavedStateHandle
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel

class SampleMVVMViewModel(
    val savedStateHandle: SavedStateHandle
) : BaseViewModel() {
    fun onMenuClick() {

    }

    fun onMenuClick(item: MenuItem): Boolean {
        when(item.itemId) {

        }
        return true
    }
}