package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.client.DataFlow
import kim.jeonghyeon.client.ScreenResult
import kim.jeonghyeon.const.DeeplinkUrl
import kim.jeonghyeon.net.RedirectionType
import kim.jeonghyeon.sample.api.SampleApi
import kim.jeonghyeon.sample.di.serviceLocator
import kotlinx.serialization.InternalSerializationApi

/**
 * this shows how to use deeplink
 * for [RedirectionType.retry], refer to [SampleViewModel.navigateToSignInOnInitialTimeIfNotSignedIn]
 *
 * Deeplink source
 * - External : deeplink from outside. doesn't support redirection
 * - Client : client create deeplink by itself
 * - Server : Server response deeplink, and client navigate to the link
 *
 * Destination
 * - Root Screen : if it's root screen. clear all screen. and show root screen
 * - Screen : open the screen, but if it's already shown, doesn't open. but just deliver deeplink
 * - url : if it's not app deeplink but just link. open web browser
 *
 * There are cases that for using A feature, B, C feature is required.
 * in the case, while entering A, check B,C is required, and B, C is already done.
 * if B, C is not yet done. navigate to B, C.
 * after complete B,C, user can use A.
 * for this case, we don't need to add logic to each screen.
 * and [signInRequired] shows how to process this case with simple way
 *
 *
 */
class DeeplinkViewModel(private val api: SampleApi = serviceLocator.sampleApi) : ModelViewModel() {

    //todo [KSA-48] support localization on kotlin side
    override val title: String = "Deeplink"

    override val signInRequired: Boolean = true

    val deeplinkSubResult by add { DataFlow<String>() }
    val deeplinkSubRequest by add { DataFlow<String>() }


    fun onClickClientDeeplink() {
        navigateToDeeplink(DeeplinkUrl.DEEPLINK_PATH_SIGN_UP)
    }

    fun onClickServerDeeplink() {
        status.load {
            api.testDeeplink()
        }
    }

    /**
     * clear all screen. and show main only
     */
    fun onClickGoToHome() {
        navigateToDeeplink(DeeplinkUrl.DEEPLINK_PATH_HOME)
    }

    /**
     * same function can be handled on server side as well by [RedirectionType.redirectionUrl]
     */
    fun onClickGoToSignInThenGoHome() {
        status.load {
            val result = navigateToDeeplinkForResult(DeeplinkUrl.DEEPLINK_PATH_SIGN_IN)
            if (result.isOk) {
                navigateToDeeplink(DeeplinkUrl.DEEPLINK_PATH_HOME)
            }
        }
    }

    /**
     * result listening is not supported
     */
    fun onClickGoogleUrl() {
        navigateToDeeplink("https://google.com")
    }

    /**
     * as [ScreenResult.data] is any? and should convert the type,
     * If you want type-safe. define data on [DeeplinkSubViewModel] and collect it directly instead using [navigateForResult]
     *
     */
    fun onClickNavigateByDeeplinkOnly() {
        status.load {
            val result: ScreenResult = navigateForResult(DeeplinkSubViewModel(deeplinkSubRequest.value?:""))
            if (result.isOk) {
                deeplinkSubResult.setValue(result.data as? String ?: "")
            }
        }

    }
}
