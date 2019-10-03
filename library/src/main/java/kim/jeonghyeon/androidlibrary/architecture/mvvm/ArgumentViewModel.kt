package kim.jeonghyeon.androidlibrary.architecture.mvvm

import androidx.navigation.NavArgs

open class ArgumentViewModel<T : NavArgs> : BaseViewModel() {
    lateinit var args: T
}