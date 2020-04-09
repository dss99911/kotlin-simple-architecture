package kim.jeonghyeon.sample

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel
import kim.jeonghyeon.androidlibrary.extension.koin
import org.koin.core.Koin
import org.koin.core.parameter.parametersOf

class MainViewModel : BaseViewModel() {
    val test = "Test"
}
