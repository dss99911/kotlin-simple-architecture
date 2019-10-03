package kim.jeonghyeon.sample.mvvm

import android.view.MenuItem
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel

class SampleViewModel(
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    //todo check if this data is fetched from savedState and arguments both.
    val id: String = savedStateHandle["id"] ?: throw IllegalArgumentException("id is null")
    val idLiveData: LiveData<String> = savedStateHandle.getLiveData("id")

    fun onMenuClick(item: MenuItem): Boolean {
        when(item.itemId) {

        }
        return true
    }
}