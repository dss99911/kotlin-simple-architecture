package kim.jeonghyeon.androidlibrary.sample.viewmodel

import androidx.lifecycle.ViewModel
import kim.jeonghyeon.androidlibrary.sample.room.repository.UserRepository

class SampleViewModel(private val repository: UserRepository) : ViewModel()