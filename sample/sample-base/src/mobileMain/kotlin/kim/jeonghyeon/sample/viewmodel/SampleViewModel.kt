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

/**
 * used for model page viewModels.
 * the reason to use this is to show different drawer for view page and model page on compose
 */
open class ModelViewModel(preference: Preference = serviceLocator.preference) : SampleViewModel(preference) {

    data class ViewModelItem(val generate: () -> SampleViewModel)

    companion object {
        /**
         * used to show on UI and create on click
         */
        val items: List<ViewModelItem> = listOf(
            ViewModelItem { ApiSingleViewModel() },
            ViewModelItem { ApiSequentialViewModel() },
            ViewModelItem { ApiParallelViewModel() },
            ViewModelItem { ApiPollingViewModel() },
            ViewModelItem { DbSimpleViewModel() },
            ViewModelItem { ApiDbViewModel() },
            ViewModelItem { ApiHeaderViewModel() },
            ViewModelItem { ApiAnnotationViewModel() },
            ViewModelItem { ApiExternalViewModel() },
            ViewModelItem { UserViewModel() },
            ViewModelItem { ApiBindingViewModel() },
            ViewModelItem { DeeplinkViewModel() },
            ViewModelItem { SearchViewModel() },
        )
    }

}

/**
 * used for view page viewModels
 * the reason to use this is to show different drawer for view page and model page
 */
open class ViewViewModel(preference: Preference = serviceLocator.preference) : SampleViewModel(preference) {
    companion object {
        /**
         * used to show on UI and create on click
         */
        val items = listOf<ModelViewModel.ViewModelItem>()
    }

}

open class SampleViewModel(private val preference: Preference = serviceLocator.preference) : BaseViewModel() {
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

            initStatus.value = Resource.Error(DeeplinkError(info)) {
                //this is called when deeplink screen result is ok or click retry button on error ui.
                initStatus.value = Resource.Success(null)//reset status.
                navigateToSignInOnInitialTimeIfNotSignedIn()
            }
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
            initStatus.value = Resource.Error(DeeplinkError(info)) {
                initStatus.value = Resource.Success(null)//reset status.
                status.retryOnError()
            }
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

            this@SampleViewModel.status.value = Resource.Error(DeeplinkError(info)) {
                status.retryOnError()
            }
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