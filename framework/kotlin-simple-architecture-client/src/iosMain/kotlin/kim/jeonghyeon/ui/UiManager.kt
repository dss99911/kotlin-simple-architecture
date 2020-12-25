package kim.jeonghyeon.ui

import kim.jeonghyeon.application
import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.client.Deeplink
import kim.jeonghyeon.client.DeeplinkNavigator
import kim.jeonghyeon.client.Navigator
import kotlinx.coroutines.GlobalScope
import platform.UIKit.UIApplication

open class UiManager {
    //as this is not recognized on swift.
    val deeplinkNavigator = DeeplinkNavigator
    val navigator = Navigator
    val globalScope = GlobalScope

    fun initialize(app: UIApplication) {
        application = app
    }
}