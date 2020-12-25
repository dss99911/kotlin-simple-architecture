package kim.jeonghyeon.base

import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.client.DataFlow

class HomeViewModel : BaseViewModel() {
    val world by add { DataFlow("World") }
}