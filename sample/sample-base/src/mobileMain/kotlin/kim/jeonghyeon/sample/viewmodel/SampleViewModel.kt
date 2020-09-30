package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.client.ScreenResult
import kim.jeonghyeon.client.call
import kim.jeonghyeon.net.error.ApiErrorBody
import kim.jeonghyeon.pergist.Preference
import kim.jeonghyeon.pergist.getUserToken
import kim.jeonghyeon.sample.di.serviceLocator
import kim.jeonghyeon.type.Resource
import kim.jeonghyeon.type.ResourceError
import kotlinx.coroutines.flow.collect
import kim.jeonghyeon.net.error.isApiErrorOf
import kim.jeonghyeon.util.log
import kotlinx.coroutines.launch

open class SampleViewModel(private val preference: Preference) : BaseViewModel() {

    //todo required for ios to create instance, currently kotlin doesn't support predefined parameter
    // if it's supported, remove this
    constructor(): this(serviceLocator.preference)
    /**
     * if the screen need sign-in, set this true by overrides
     * then when onInitialized() is called, before initializing,
     * check signed-in or not, and redirect to sign in page
     * after sign-in completed, initializing will get started
     */
    open val signInRequired = false

    val goSignIn = dataFlow<SignInResultListener?>(null)

    final override fun onInitialized() {
        checkSignInRequired()
        collectErrorStatusForSignIn()
    }

    fun checkSignInRequired() {
        val userToken = preference.getUserToken()
        if (signInRequired && userToken == null) {
            goSignIn.call(object : SignInResultListener {
                override fun onSignInResult(result: ScreenResult) {
                    if (result.isOk) {
                        onInit()
                    } else {
                        //in case, onInit doesn't use initStatus,
                        // if Error is set before go to signIn page,
                        // then error will be shown continuously.
                        // so, check if it's cancelled, then set error.
                        initStatus.value = Resource.Error(ResourceError("Sign in is required"), null) {
                            initStatus.value = Resource.Start
                            checkSignInRequired()
                        }
                    }
                }
            })
        } else {
            onInit()
        }
    }

    private fun collectErrorStatusForSignIn() {
        if (isSignViewModel()) {
            return
        }

        scope.launch {
            //we already check signInRequired and navigate to sign in screen.
            //but, in case token is expired. we need api call to check signin.
            initStatus.collect {
                it.onError { error, _, _ ->
                    if (error.isApiErrorOf(ApiErrorBody.Unauthorized)) {
                        goSignIn.call(object : SignInResultListener {
                            override fun onSignInResult(result: ScreenResult) {
                                log.i("onResult")
                                if (result.isOk) {
                                    log.i("onResult, retry()")
                                    initStatus.value.retryOnError()
                                }
                            }
                        })
                    }
                }
            }
        }

        scope.launch {
            status.collect {
                it.onError { error, _, _ ->
                    if (error.isApiErrorOf(ApiErrorBody.Unauthorized)) {
                        goSignIn.call(object : SignInResultListener {
                            override fun onSignInResult(result: ScreenResult) {
                                status.value = Resource.Start//hide error view
                                //do nothing. let user decide to retry or not.
                                //normally, button click action is performed by user
                                //and user may change mind not to click the button after sign-in
                            }
                        })
                    }
                }
            }
        }
    }


    fun isSignViewModel(): Boolean = this@SampleViewModel is SignInViewModel || this@SampleViewModel is SignUpViewModel

    /**
     * as [onInitialized] check if sign-in or not. [onInit] will be used for initializing
     */
    open fun onInit() {

    }
}

interface SignInResultListener {
    fun onSignInResult(result: ScreenResult)
}