package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.client.ScreenResult
import kim.jeonghyeon.net.error.ApiError
import kim.jeonghyeon.net.error.ApiErrorBody
import kim.jeonghyeon.sample.api.SerializableUserDetail
import kim.jeonghyeon.sample.di.serviceLocator
import kim.jeonghyeon.sample.repository.UserRepository
import kim.jeonghyeon.type.Resource
import kim.jeonghyeon.util.log

class SignInViewModel(val userRepo: UserRepository) : SampleViewModel() {

    //todo required for ios to create instance, currently kotlin doesn't support predefined parameter
    // if it's supported, remove this
    constructor(): this(serviceLocator.userRepository)

    val inputId = dataFlow("")
    val inputPassword = dataFlow("")

    fun onClickSignIn() = status.load {
        userRepo.signIn(inputId.value, inputPassword.value)
        goBackWithOk()
    }

    fun onSignUpResult(result: ScreenResult) {
        if (result.isOk) {
            goBackWithOk()
        }
    }
}