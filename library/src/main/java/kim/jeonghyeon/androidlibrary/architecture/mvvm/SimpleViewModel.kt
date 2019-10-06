package kim.jeonghyeon.androidlibrary.architecture.mvvm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.NavArgs

open class SimpleViewModel<P : ViewModel, A : NavArgs>(val savedStateHandle: SavedStateHandle)
    : BaseViewModel()
{
    lateinit var args: A
    lateinit var parent: P
}
