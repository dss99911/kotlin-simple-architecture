package kim.jeonghyeon.androidlibrary.architecture.mvvm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.NavArgs

open class NavArgsViewModel<T : NavArgs>(savedStateHandle: SavedStateHandle) : SimpleViewModel<ViewModel, T>(
    savedStateHandle
)