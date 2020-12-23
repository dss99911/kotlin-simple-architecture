package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.client.flowViewModel

/**
 * this just used for navigation stack
 **/
class HomeViewModel : SampleViewModel() {

    //todo [KSA-48] support localization on kotlin side
    override val title: String = "Home"

    val currentTabIndex by add { flowViewModel(0) }

}