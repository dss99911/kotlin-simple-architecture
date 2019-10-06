package kim.jeonghyeon.androidlibrary.architecture.mvvm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.NavArgs

open class ParentViewModel<P : ViewModel>(savedStateHandle: SavedStateHandle) : SimpleViewModel<P, NavArgs>(savedStateHandle)