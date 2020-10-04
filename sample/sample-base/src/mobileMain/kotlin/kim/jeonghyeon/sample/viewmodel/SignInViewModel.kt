package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.client.DataFlow
import kim.jeonghyeon.client.ScreenResult
import kim.jeonghyeon.sample.di.serviceLocator
import kim.jeonghyeon.sample.repository.UserRepository

class SignInViewModel(val userRepo: UserRepository) : SampleViewModel() {

    //todo required for ios to create instance, currently kotlin doesn't support predefined parameter
    // if it's supported, remove this
    constructor() : this(serviceLocator.userRepository)

    val inputId by add { DataFlow<String>() }
    val inputPassword by add { DataFlow<String>() }

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