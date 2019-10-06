package kim.jeonghyeon.sample.mvvm

import android.view.MenuItem
import androidx.lifecycle.SavedStateHandle
import kim.jeonghyeon.androidlibrary.architecture.mvvm.EmptyViewModel

class SampleMVVMViewModel(
    savedStateHandle: SavedStateHandle
) : EmptyViewModel(savedStateHandle) {
    fun onMenuClick() {

    }

    fun onMenuClick(item: MenuItem): Boolean {
        when(item.itemId) {

        }
        return true
    }
}