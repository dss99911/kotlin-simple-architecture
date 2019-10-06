package kim.jeonghyeon.sample.mvvm

import androidx.lifecycle.SavedStateHandle
import kim.jeonghyeon.androidlibrary.architecture.mvvm.NavArgsViewModel

class NavigationViewModel(savedStateHandle: SavedStateHandle) :
    NavArgsViewModel<NavigationFragmentArgs>(savedStateHandle) {

}