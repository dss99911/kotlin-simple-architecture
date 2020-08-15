package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.net.error.ApiError
import kim.jeonghyeon.net.error.ApiErrorBody
import kim.jeonghyeon.sample.api.SignBasicApi
import kim.jeonghyeon.sample.api.SignDigestApi
import kim.jeonghyeon.sample.api.UserApi
import kim.jeonghyeon.sample.api.UserDetail
import kim.jeonghyeon.sample.di.serviceLocator
import kim.jeonghyeon.type.Resource
import kotlinx.coroutines.flow.MutableStateFlow

class DigestSignInViewModel(
    val signApi: SignDigestApi = serviceLocator.signDigestApi,
    val userApi: UserApi = serviceLocator.userApi
) : BaseViewModel() {
    val inputId = MutableStateFlow("")
    val inputPassword = MutableStateFlow("")
    val user = MutableStateFlow<UserDetail?>(null)

    override fun onInitialized() {
        getUserDetail()
    }

    fun onSignedUp() {
        getUserDetail()
    }

    fun onClickLogOut() = status.load {
        signApi.signOut()
        getUserDetail()
    }

    fun onClickSignIn() {
        if (inputId.value.isBlank()) {
            toast("Please input id")
            return
        }

        if (inputPassword.value.isBlank()) {
            toast("Please input Password")
            return
        }

        status.load {
            signApi.signIn(inputId.value, inputPassword.value)
            getUserDetail()
        }
    }

    private fun getUserDetail() {
        user.load(initStatus, work = {
            userApi.getUser()
        }) {
            //if unauthorized error, show login page.
            if (it.isErrorOf<ApiError>()) {
                if (it.errorOf<ApiError>().code == ApiErrorBody.Unauthorized.code) {
                    return@load Resource.Success<UserDetail?>(null)
                }
            }
            it
        }
    }
}