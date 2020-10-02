package kim.jeonghyeon.sample.viewmodel

import io.ktor.client.features.json.serializer.*
import io.ktor.http.content.*
import kim.jeonghyeon.client.DeeplinkNavigation
import kim.jeonghyeon.client.DeeplinkResultListener
import kim.jeonghyeon.client.ScreenResult
import kim.jeonghyeon.const.DeeplinkUrl
import kim.jeonghyeon.extension.toJsonString
import kim.jeonghyeon.net.RedirectionType
import kim.jeonghyeon.sample.api.SampleApi
import kim.jeonghyeon.sample.di.serviceLocator
import kim.jeonghyeon.util.log
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

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
class DeeplinkViewModel(private val api: SampleApi) : SampleViewModel() {
    override val signInRequired: Boolean = true

    val deeplinkSubResult = dataFlow("")
    val deeplinkSubRequest = dataFlow("")

    //todo required for ios to create instance, currently kotlin doesn't support predefined parameter
    // if it's supported, remove this
    constructor() : this(serviceLocator.sampleApi)


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
        navigateToDeeplink(DeeplinkUrl.DEEPLINK_PATH_SIGN_IN) {
            if (it.isOk) {
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
     * It's boilerplate code below just for navigating to a screen.
     *
     * //viweModel
     * val goToSignIn = dataFlow<Unit?>(null)
     *
     * fun process() {
     *     goToSignIn.value = Unit
     * }
     *
     * //for android
     * viewModel.goToSignIn.collect {
     *     push(SignInScreen())
     * }
     * //for ios
     * viewModel.goToSignIn.watch { data in
     *     guard let data = data else { return }
     *     navigator.navigate {
     *         SignInScreen()
     *     }
     * }
     *
     * so, this function shows without the code above.
     * this navigates to screen by deeplink only.
     *
     * send parameter by deeplink query param
     * receive data by result listener
     *
     * todo Automatic deeplink https://hyun.myjetbrains.com/youtrack/issue/KSA-133
     *  - need to restrict some screen from external deeplink
     *  for this, add a field on viewModel. if this is allowed from external deeplink
     *  - generate code for all the screen. so, no need to connect deeplink with screen.
     *  research on swift as well if it's possible or not.
     */
    @OptIn(InternalSerializationApi::class)
    fun onClickNavigateByDeeplinkOnly() {
        navigateToDeeplink(DeeplinkUrl.DEEPLINK_PATH_DEEPLINK_SUB, deeplinkSubRequest.value) {
            if (it.isOk) {
                deeplinkSubResult.value = it.dataOf(DeeplinkSubViewModel.RESPONSE_TYPE)
            }
        }
    }
}
