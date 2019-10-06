package kim.jeonghyeon.androidlibrary.architecture.mvvm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.NavArgs

open class EmptyViewModel(savedStateHandle: SavedStateHandle) : SimpleViewModel<ViewModel, NavArgs>(
    savedStateHandle
)