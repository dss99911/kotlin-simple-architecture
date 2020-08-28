package kim.jeonghyeon.sample.viewmodel

import androidLibrary.sample.samplebase.generated.SimpleConfig
import io.ktor.http.*
import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.sample.di.serviceLocator
import kim.jeonghyeon.sample.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow


class SignUpViewModel(
    val userRepo: UserRepository = serviceLocator.userRepository
) : BaseViewModel() {
    val inputId = MutableStateFlow("")
    val inputName = MutableStateFlow("")
    val inputPassword = MutableStateFlow("")

    fun onClickSignUp() {
        status.load {
            userRepo.signUp(inputId.value, inputPassword.value, inputName.value)
            finishSuccess()
        }
    }

    fun onClickGoogle() {
        status.load {
            userRepo.signGoogle(DEEPLINK_PATH)
        }
    }

    fun onClickFacebook() {
        status.load {
            userRepo.signFacebook(DEEPLINK_PATH)
        }
    }

    override fun onDeeplinkReceived(url: Url) {
        userRepo.onOAuthDeeplinkReceived(url)
        finishSuccess()
    }

    private fun finishSuccess() {
        toast("success to sign up")
        goBack()
    }


    companion object {
        const val DEEPLINK_PATH = "${SimpleConfig.deeplinkPrePath}/signUp"
    }
}