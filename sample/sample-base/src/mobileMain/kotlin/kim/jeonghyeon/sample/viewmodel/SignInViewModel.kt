package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.auth.SignApi
import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.net.error.ApiError
import kim.jeonghyeon.net.error.ApiErrorBody
import kim.jeonghyeon.sample.api.SerializableUserDetail
import kim.jeonghyeon.sample.di.serviceLocator
import kim.jeonghyeon.sample.repository.UserRepository
import kim.jeonghyeon.type.Resource
import kotlinx.coroutines.flow.MutableStateFlow

class SignInViewModel(
    val signApi: SignApi = serviceLocator.signApi,
    val userRepo: UserRepository = serviceLocator.userRepository
) : BaseViewModel() {
    val inputId = MutableStateFlow("")
    val inputPassword = MutableStateFlow("")
    val user = MutableStateFlow<SerializableUserDetail?>(null)

    override fun onInitialized() {
        getUserDetail()
    }

    fun onClickLogOut() = status.load {
        signApi.signOut()
        userRepo.invalidateUser()
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
            userRepo.invalidateUser()
        }
    }

    private fun getUserDetail() {
        user.load(initStatus, userRepo.userDetail) {
            //if unauthorized error, show login page.
            if (it.isErrorOf<ApiError>()) {
                if (it.errorOf<ApiError>().code == ApiErrorBody.Unauthorized.code) {
                    return@load Resource.Success<SerializableUserDetail?>(null)
                }
            }
            it
        }
    }
}