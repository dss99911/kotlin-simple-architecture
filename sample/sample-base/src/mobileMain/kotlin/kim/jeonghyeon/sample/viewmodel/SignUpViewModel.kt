package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.sample.api.SignApi
import kim.jeonghyeon.sample.di.serviceLocator
import kotlinx.coroutines.flow.MutableStateFlow

class SignUpViewModel(val onSignedUp: () -> Unit, val api: SignApi = serviceLocator.signApi) : BaseViewModel() {
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
            api.signUp(inputId.value, inputName.value, inputPassword.value)
            toast("success to sign up")
            goBack()
            onSignedUp()
        }
    }
}