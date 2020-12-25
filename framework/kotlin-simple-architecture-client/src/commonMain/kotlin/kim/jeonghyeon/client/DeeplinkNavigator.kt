package kim.jeonghyeon.client

import io.ktor.http.*
import kim.jeonghyeon.auth.loadUrlInBrowser
import kotlin.native.concurrent.ThreadLocal
import kotlin.reflect.KClass

/**
 * support single stack. as android, ios approach to refer data is different
 */
@ThreadLocal
object DeeplinkNavigator {
    /**
     * if [BaseActivity.deeplinks] is set, this is automatically set.
     */
    var deeplinks: List<Deeplink> = emptyList()

    /**
     * call this function from external like clicking link on web
     */
    fun navigateToDeeplinkFromExternal(url: String) {
        val deeplink = deeplinks.firstOrNull { it.isPublic && it.matches(url) }
            ?: return//this is from external. so, doesn't open web browser

        //single top
        var current = Navigator.current
        if (current::class != deeplink.kClass) {
            current = deeplink.viewModel().also { viewModel ->
                if (viewModel::class == Navigator.root::class) {
                    Navigator.clearAndNavigate(viewModel)
                } else {
                    Navigator.navigate(viewModel)
                }
            }
        }

        current.onDeeplinkReceived(Url(url))
    }

    /**
     * call this function from viewModel, or backend
     * @receiver current viewModel
     */
    fun BaseViewModel.navigateToDeeplinkFromInternal(navigation: DeeplinkNavigation) {
        val deeplink = deeplinks.firstOrNull { it.matches(navigation.url) }

        if (deeplink == null) {
            //if deeplink matched screen not exists, open web browser
            loadUrlInBrowser(navigation.url)
            return
        }

        //single top
        var current = Navigator.current
        if (current::class != deeplink.kClass) {
            current = deeplink.viewModel().also { viewModel ->
                if (viewModel::class == Navigator.root::class) {
                    Navigator.clearAndNavigate(viewModel)
                } else {
                    if (navigation.resultListener == null) {
                        navigate(viewModel)
                    } else {
                        launch {
                            val result = navigateForResult(viewModel)
                            navigation.resultListener.onDeeplinkResult(result)
                        }
                    }

                }
            }
        }

        current.onDeeplinkReceived(Url(navigation.url))
    }
}

/**
 * @param isPublic If navigated from external, and this is false. then doesn't allow to navigate to the page
 */
class Deeplink(
    val path: String,
    val kClass: KClass<*>,
    val isPublic: Boolean = true,
    val viewModel: () -> BaseViewModel
) {
    fun matches(url: String): Boolean = url.startsWith(path)
}
