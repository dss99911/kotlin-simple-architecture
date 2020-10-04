package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.const.DeeplinkUrl
import kim.jeonghyeon.net.DeeplinkError
import kim.jeonghyeon.net.DeeplinkInfo
import kim.jeonghyeon.net.RedirectionInfo
import kim.jeonghyeon.net.RedirectionType
import kim.jeonghyeon.net.error.ApiError
import kim.jeonghyeon.net.error.ApiErrorBody
import kim.jeonghyeon.pergist.Preference
import kim.jeonghyeon.pergist.getUserToken
import kim.jeonghyeon.sample.di.serviceLocator
import kim.jeonghyeon.type.Resource

open class SampleViewModel(private val preference: Preference) : BaseViewModel() {

    //todo required for ios to create instance, currently kotlin doesn't support predefined parameter
    // if it's supported, remove this
    constructor() : this(serviceLocator.preference)

    /**
     * if the screen need sign-in, set this true by overrides
     * then when onInitialized() is called, before initializing,
     * check signed-in or not, and redirect to sign in page
     * after sign-in completed, initializing will get started
     */
    open val signInRequired = false

    final override fun onInitialized() {
        navigateToSignInOnInitialTimeIfNotSignedIn()
        navigateToSignInOnUnauthroized()
    }

    fun navigateToSignInOnInitialTimeIfNotSignedIn() {
        if (signInRequired && preference.getUserToken() == null) {
            val info = DeeplinkInfo(
                DeeplinkUrl.DEEPLINK_PATH_SIGN_IN,
                "Sign in is required",
                RedirectionInfo(RedirectionType.retry)
            )

            initStatus.setValue(Resource.Error(DeeplinkError(info), null) {
                //this is called when deeplink screen result is ok or click retry button on error ui.
                initStatus.setValue(Resource.Success(null))//reset status.
                navigateToSignInOnInitialTimeIfNotSignedIn()
            })
        } else {
            onInit()
        }
    }

    private fun navigateToSignInOnUnauthroized() {
        if (isSignViewModel()) {
            return
        }

        initStatus.collectOnViewModel { status ->
            val error = status.errorOrNullOf<ApiError>() ?: return@collectOnViewModel

            //we already check signInRequired and navigate to sign in screen.
            //but, in case token is expired. we need api call to check signin.
            if (error.body != ApiErrorBody.Unauthorized) {
                return@collectOnViewModel
            }

            val info = DeeplinkInfo(
                DeeplinkUrl.DEEPLINK_PATH_SIGN_IN,
                error.errorMessage,
                RedirectionInfo(RedirectionType.retry)
            )
            initStatus.setValue(Resource.Error(DeeplinkError(info), null) {
                status.retryOnError()
            })
        }

        status.collectOnViewModel { status ->
            val error = status.errorOrNullOf<ApiError>() ?: return@collectOnViewModel
            if (error.body != ApiErrorBody.Unauthorized) {
                return@collectOnViewModel
            }

            val info = DeeplinkInfo(
                DeeplinkUrl.DEEPLINK_PATH_SIGN_IN,
                error.errorMessage,

                //do nothing. let user decide to retry or not.
                //normally, button click action is performed by user
                //and user may change mind not to click the button after sign-in
                RedirectionInfo(RedirectionType.none)
            )

            this@SampleViewModel.status.setValue(Resource.Error(DeeplinkError(info), null) {
                status.retryOnError()
            })
        }
    }

    private fun isSignViewModel(): Boolean =
        this@SampleViewModel is SignInViewModel || this@SampleViewModel is SignUpViewModel

    /**
     * as [onInitialized] check if sign-in or not. [onInit] will be used for initializing
     */
    open fun onInit() {

    }
}