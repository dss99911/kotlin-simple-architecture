package kim.jeonghyeon.sample.viewmodel

import io.ktor.http.*
import kim.jeonghyeon.client.DataFlow
import kim.jeonghyeon.sample.di.serviceLocator
import kim.jeonghyeon.sample.repository.UserRepository

class SignUpViewModel(val userRepo: UserRepository = serviceLocator.userRepository) : ModelViewModel() {

    //todo [KSA-48] support localization on kotlin side
    override val title: String = "Sign Up"

    val inputId by add { DataFlow<String>() }
    val inputName by add { DataFlow<String>() }
    val inputPassword by add { DataFlow<String>() }

    fun onClickSignUp() {
        status.load {
            userRepo.signUp(inputId.value, inputPassword.value, inputName.value)
            finishSuccess()
        }
    }

    fun onClickGoogle() {
        status.load {
            userRepo.signGoogle()
        }
    }

    fun onClickFacebook() {
        status.load {
            userRepo.signFacebook()
        }
    }

    override fun onDeeplinkReceived(url: Url) {
        try {
            userRepo.onOAuthDeeplinkReceived(url)
            finishSuccess()
        } catch (e: Exception) {
        }
    }

    private fun finishSuccess() {
        //todo toast is not supported on ios. how to show ui even while screen is changed.
//        toast("success to sign up")
        goBackWithOk()
    }
}