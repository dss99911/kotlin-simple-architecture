package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.client.viewModelFlow

/**
 * this just used for navigation stack
 **/
class HomeViewModel : SampleViewModel() {

    //todo [KSA-48] support localization on kotlin side
    override val title: String = "Home"

    val currentTabIndex = viewModelFlow(0)

}