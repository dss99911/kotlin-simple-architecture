package kim.jeonghyeon.base

import kim.jeonghyeon.client.BaseViewModel

class HomeViewModel : BaseViewModel() {
    val world = viewModelFlow("world")
}