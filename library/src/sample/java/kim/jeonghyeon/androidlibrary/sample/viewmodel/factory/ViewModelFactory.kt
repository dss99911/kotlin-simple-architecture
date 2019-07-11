package kim.jeonghyeon.androidlibrary.sample.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kim.jeonghyeon.androidlibrary.sample.room.repository.UserRepository
import kim.jeonghyeon.androidlibrary.sample.viewmodel.SampleViewModel

class ViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SampleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SampleViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}