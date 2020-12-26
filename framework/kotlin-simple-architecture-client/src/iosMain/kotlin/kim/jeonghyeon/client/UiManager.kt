package kim.jeonghyeon.client

import kim.jeonghyeon.application
import kotlinx.coroutines.GlobalScope
import platform.UIKit.UIApplication

actual class UiManager actual constructor() {

    val deeplinkNavigator = DeeplinkNavigator
    val navigator = Navigator
    val globalScope = GlobalScope

    fun initialize(app: UIApplication) {
        application = app
    }
}