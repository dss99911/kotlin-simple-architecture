package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.client.DataFlow
import kim.jeonghyeon.client.ScreenResult
import kim.jeonghyeon.sample.di.serviceLocator
import kim.jeonghyeon.sample.repository.UserRepository

class SignInViewModel(val userRepo: UserRepository = serviceLocator.userRepository) : ModelViewModel() {

    //todo [KSA-48] support localization on kotlin side
    override val title: String = "Sign in"

    val inputId by add { DataFlow<String>() }
    val inputPassword by add { DataFlow<String>() }

    fun onClickSignIn() = status.load {
        userRepo.signIn(inputId.value, inputPassword.value)
        goBackWithOk()
    }

    fun onClickSignUp() {
        status.load {
            val result = navigateForResult(SignUpViewModel())
            if (result.isOk) {
                goBackWithOk()
            }
        }
    }
}