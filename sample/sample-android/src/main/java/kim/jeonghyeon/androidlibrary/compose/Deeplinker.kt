package kim.jeonghyeon.androidlibrary.compose

import io.ktor.http.*
import kim.jeonghyeon.androidlibrary.extension.startActivityUrl
import kim.jeonghyeon.client.DeeplinkNavigation
import kotlin.reflect.KClass

internal object Deeplinker {
    /**
     * if [BaseActivity.deeplinks] is set, this is automatically set.
     */
    internal var deeplinks: Map<String, Pair<KClass<*>, () -> Screen>> = emptyMap()

    fun navigateToDeeplink(url: String) {
        val classAndScreen = deeplinks.entries
            .firstOrNull {
                url.startsWith(it.key)?:false
            }?.value

        if (classAndScreen == null) {
            //if deeplink matched screen not exists, open web browser
            startActivityUrl(url)
            return
        }

        //single top
        var last = ScreenStack.last()
        if (last::class != classAndScreen.first) {
            last = classAndScreen.second().also { screen ->
                if (screen.isRoot) {
                    screen.clearAndPush()
                } else {
                    screen.push()
                }
            }
        }

        last.onDeeplinkReceived(Url(url))
    }

    fun navigateToDeeplink(currentScreen: Screen, navigation: DeeplinkNavigation) {
        val classAndScreen = deeplinks.entries
            .firstOrNull {
                navigation.url.startsWith(it.key)
            }?.value

        if (classAndScreen == null) {
            //if deeplink matched screen not exists, open web browser
            startActivityUrl(navigation.url)
            return
        }

        //single top
        var last = ScreenStack.last()
        if (last::class != classAndScreen.first) {
            last = classAndScreen.second().also { screen ->
                if (screen.isRoot) {
                    screen.clearAndPush()
                } else {
                    currentScreen.push(screen) {
                        if (it.isOk) {
                            navigation.resultListener?.onDeeplinkResult(it)
                        }
                    }
                }
            }
        }

        last.onDeeplinkReceived(Url(navigation.url))
    }
}