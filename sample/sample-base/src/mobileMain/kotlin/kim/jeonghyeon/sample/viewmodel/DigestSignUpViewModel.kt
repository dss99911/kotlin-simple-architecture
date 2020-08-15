package kim.jeonghyeon.sample.viewmodel

import io.ktor.util.InternalAPI
import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.sample.api.SignBasicApi
import kim.jeonghyeon.sample.api.SignDigestApi
import kim.jeonghyeon.sample.di.serviceLocator
import kotlinx.coroutines.flow.MutableStateFlow

class DigestSignUpViewModel(val onSignedUp: () -> Unit, val api: SignDigestApi = serviceLocator.signDigestApi) : BaseViewModel() {
    val inputId = MutableStateFlow("")
    val inputName = MutableStateFlow("")
    val inputPassword = MutableStateFlow("")

    fun onClickSignUp() {
        if (inputId.value.isBlank()) {
            toast("Please input Password")
            return
        }

        if (inputName.value.isBlank()) {
            toast("Please input name")
            return
        }

        if (inputPassword.value.isBlank()) {
            toast("Please input Password")
            return
        }

        status.load {
            api.signUp(inputId.value, inputPassword.value, inputName.value)
            toast("success to sign up")
            goBack()
            onSignedUp()
        }
    }
}