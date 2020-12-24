package kim.jeonghyeon.sample.viewmodel

import io.ktor.http.*
import kim.jeonghyeon.client.viewModelFlow
import kim.jeonghyeon.sample.di.serviceLocator
import kim.jeonghyeon.sample.repository.UserRepository
import kim.jeonghyeon.type.Status
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.merge

class SignUpViewModel(val userRepo: UserRepository = serviceLocator.userRepository) : ModelViewModel() {

    //todo [KSA-48] support localization on kotlin side
    override val title: String = "Sign Up"

    val inputId = viewModelFlow<String>()
    val inputName = viewModelFlow<String>()
    val inputPassword = viewModelFlow<String>()

    fun onClickSignUp() {
        status.load {
            userRepo.signUp(inputId.valueOrNull, inputPassword.valueOrNull, inputName.valueOrNull)
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

// TODO reactive way.
//class SignUpViewModel2(val userRepo: UserRepository = serviceLocator.userRepository) : ModelViewModel() {
//
//    //todo [KSA-48] support localization on kotlin side
//    override val title: String = "Sign Up"
//
//    val inputId by add { viewModelFlow<String>() }
//    val inputName by add { viewModelFlow<String>() }
//    val inputPassword by add { viewModelFlow<String>() }
//    val clickSignUp = viewModelFlow<Unit>()
//    val clickGoogle = viewModelFlow<Unit>()
//    val clickFacebook = viewModelFlow<Unit>()
//
//    override val status: MutableSharedFlow<Status> by add {
//        merge(
//            clickSignUp
//                .mapInIdle {
//                    userRepo.signUp(inputId.valueOrNull, inputPassword.valueOrNull, inputName.valueOrNull)
//                    finishSuccess()
//                },
//            clickGoogle.mapInIdle { userRepo.signGoogle() },
//            clickFacebook.mapInIdle { userRepo.signFacebook() }
//        ).toStatus()
//    }
//
//    override fun onDeeplinkReceived(url: Url) {
//        try {
//            userRepo.onOAuthDeeplinkReceived(url)
//            finishSuccess()
//        } catch (e: Exception) {
//        }
//    }
//
//    private fun finishSuccess() {
//        //todo toast is not supported on ios. how to show ui even while screen is changed.
////        toast("success to sign up")
//        goBackWithOk()
//    }
//}