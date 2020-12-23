package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.client.flowViewModel
import kim.jeonghyeon.client.valueOrNull
import kim.jeonghyeon.sample.di.serviceLocator
import kim.jeonghyeon.sample.repository.UserRepository
import kim.jeonghyeon.type.Status
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.merge

class SignInViewModel(val userRepo: UserRepository = serviceLocator.userRepository) : ModelViewModel() {

    //todo [KSA-48] support localization on kotlin side
    override val title: String = "Sign in"

    val inputId by add { flowViewModel<String>() }
    val inputPassword by add { flowViewModel<String>() }

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

class SignInViewModel2(val userRepo: UserRepository = serviceLocator.userRepository) : ModelViewModel() {

    //todo [KSA-48] support localization on kotlin side
    override val title: String = "Sign in"

    val inputId by add { flowViewModel<String>() }
    val inputPassword by add { flowViewModel<String>() }

    val clickSignIn = flowViewModel<Unit>()
    val clickSignUp = flowViewModel<Unit>()

    override val status: MutableSharedFlow<Status> by add {
        merge(
            clickSignIn
                .mapInIdle {
                    userRepo.signIn(inputId.valueOrNull, inputPassword.valueOrNull)
                    goBackWithOk()
                },
            clickSignUp
                .mapInIdle {
                    val result = navigateForResult(SignUpViewModel())
                    if (result.isOk) {
                        goBackWithOk()
                    }
                }
        ).toStatus()
    }
}