package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.client.viewModelFlow
import kim.jeonghyeon.sample.di.serviceLocator
import kim.jeonghyeon.sample.repository.UserRepository
import kim.jeonghyeon.type.Status
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.merge

class SignInViewModel(val userRepo: UserRepository = serviceLocator.userRepository) : ModelViewModel() {

    //todo [KSA-48] support localization on kotlin side
    override val title: String = "Sign in"

    val inputId = viewModelFlow<String>()
    val inputPassword = viewModelFlow<String>()

    fun onClickSignIn() = status.load {
        userRepo.signIn(inputId.valueOrNull, inputPassword.valueOrNull)
        goBackWithOk()
    }

    fun onClickSignUp() {
        status.loadInIdle {
            val result = navigateForResult(SignUpViewModel())
            if (result.isOk) {
                goBackWithOk()
            }
        }
    }
}

// TODO reactive way.
//class SignInViewModel2(val userRepo: UserRepository = serviceLocator.userRepository) : ModelViewModel() {
//
//    //todo [KSA-48] support localization on kotlin side
//    override val title: String = "Sign in"
//
//    val inputId by add { viewModelFlow<String>() }
//    val inputPassword by add { viewModelFlow<String>() }
//
//    val clickSignIn = viewModelFlow<Unit>()
//    val clickSignUp = viewModelFlow<Unit>()
//
//    override val status: MutableSharedFlow<Status> by add {
//        merge(
//            clickSignIn
//                .mapInIdle {
//                    userRepo.signIn(inputId.valueOrNull, inputPassword.valueOrNull)
//                    goBackWithOk()
//                },
//            clickSignUp
//                .mapInIdle {
//                    val result = navigateForResult(SignUpViewModel())
//                    if (result.isOk) {
//                        goBackWithOk()
//                    }
//                }
//        ).toStatus()
//    }
//}