package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.client.DataFlow

/**
 * this just used for navigation stack
 **/
class HomeViewModel : SampleViewModel() {

    //todo [KSA-48] support localization on kotlin side
    override val title: String = "Home"

    val currentTabIndex by add { DataFlow(0) }

}