package kim.jeonghyeon.sample.mvvm

import androidx.lifecycle.SavedStateHandle
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel

class NavigationViewModel(val args: NavigationFragmentArgs, val savedStateHandle: SavedStateHandle) :
    BaseViewModel()