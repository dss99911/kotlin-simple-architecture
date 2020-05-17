package kim.jeonghyeon.sample.list.simple

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel

class SimpleListViewModel : BaseViewModel() {
    val sampleList = (1..10).map { it.toString() }
}