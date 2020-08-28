package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.net.error.ApiError
import kim.jeonghyeon.net.error.ApiErrorBody
import kim.jeonghyeon.sample.api.SerializableUserDetail
import kim.jeonghyeon.sample.di.serviceLocator
import kim.jeonghyeon.sample.repository.UserRepository
import kim.jeonghyeon.type.Resource
import kotlinx.coroutines.flow.MutableStateFlow

class SignInViewModel(
    val userRepo: UserRepository = serviceLocator.userRepository
) : BaseViewModel() {
    val inputId = MutableStateFlow("")
    val inputPassword = MutableStateFlow("")
    val user = MutableStateFlow<SerializableUserDetail?>(null)

    override fun onInitialized() {
        getUserDetail()
    }

    fun onClickLogOut() = status.load {
        userRepo.signOut()
    }

    fun onClickSignIn() = status.load {
        userRepo.signIn(inputId.value, inputPassword.value)
    }

    private fun getUserDetail() = user.load(initStatus, userRepo.userDetail) {
        //if unauthorized error, show login page.
        if (it.isErrorOf<ApiError>()) {
            if (it.errorOf<ApiError>().code == ApiErrorBody.Unauthorized.code) {
                return@load Resource.Success<SerializableUserDetail?>(null)
            }
        }
        it
    }
}