package kim.jeonghyeon.sample.viewmodel

import io.ktor.http.*
import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.sample.di.serviceLocator
import samplebase.generated.SimpleConfig

class SignUpViewModel(/*val userRepo: UserRepository*/) : BaseViewModel() {

    //todo required for ios to create instance, currently kotlin doesn't support predefined parameter
    // if it's supported, remove this
//    constructor(): this(serviceLocator.userRepository)

    val inputId = dataFlow("")
    val inputName = dataFlow("")
    val inputPassword = dataFlow("")

    fun onClickSignUp() {
        status.load {
            //todo after this fixed https://youtrack.jetbrains.com/issue/KTOR-973
            // use constructor parameter serviceLocator.userRepository
            serviceLocator.userRepository.signUp(inputId.value, inputPassword.value, inputName.value)
            finishSuccess()
        }
    }

    fun onClickGoogle() {
        status.load {
            //todo after this fixed https://youtrack.jetbrains.com/issue/KTOR-973
            // use constructor parameter serviceLocator.userRepository
            serviceLocator.userRepository.signGoogle(DEEPLINK_PATH)
        }
    }

    fun onClickFacebook() {
        status.load {
            //todo after this fixed https://youtrack.jetbrains.com/issue/KTOR-973
            // use constructor parameter serviceLocator.userRepository
            serviceLocator.userRepository.signFacebook(DEEPLINK_PATH)
        }
    }

    override fun onDeeplinkReceived(url: Url) {
        //todo after this fixed https://youtrack.jetbrains.com/issue/KTOR-973
        // use constructor parameter serviceLocator.userRepository
        serviceLocator.userRepository.onOAuthDeeplinkReceived(url)
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