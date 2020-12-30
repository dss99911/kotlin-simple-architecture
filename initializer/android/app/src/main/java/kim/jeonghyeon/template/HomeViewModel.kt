package kim.jeonghyeon.template

import kim.jeonghyeon.client.BaseViewModel

class HomeViewModel : BaseViewModel() {
    val world = viewModelFlow("world")
}