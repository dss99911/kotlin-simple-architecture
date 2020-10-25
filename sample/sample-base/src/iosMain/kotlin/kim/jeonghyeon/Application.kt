package kim.jeonghyeon

import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.client.Deeplink
import kim.jeonghyeon.client.DeeplinkNavigator
import kim.jeonghyeon.client.Navigator
import kim.jeonghyeon.sample.deeplinkList
import kim.jeonghyeon.sample.viewmodel.HomeViewModel
import kim.jeonghyeon.ui.UiManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import platform.UIKit.UIApplication


//todo if this is in library module. it's not recognized.
// if it's recognized, remove this and use UiManager.
class SampleUiManager : UiManager()