package kim.jeonghyeon.sample.viewmodel

import androidLibrary.sample.samplebase.generated.SimpleConfig
import io.ktor.http.*
import kim.jeonghyeon.auth.OAuthServerName
import kim.jeonghyeon.auth.SignApi
import kim.jeonghyeon.auth.SignOAuthClient
import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.extension.toJsonString
import kim.jeonghyeon.sample.api.SerializableUserDetail
import kim.jeonghyeon.sample.di.serviceLocator
import kim.jeonghyeon.sample.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow


class SignUpViewModel(
    val api: SignApi = serviceLocator.signApi,
    val userRepo: UserRepository = serviceLocator.userRepository,
    val oauthClient: SignOAuthClient = serviceLocator.oauthClient
) : BaseViewModel() {
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
            return
        }

        status.load {
            api.signUp(
                inputId.value,
                inputPassword.value,
                SerializableUserDetail(null, inputName.value).toJsonString()
            )
            api.signIn(inputId.value, inputPassword.value)
            finishSuccess()
        }
    }

    fun onClickGoogle() {
        status.load {
            oauthClient.signUp(OAuthServerName.GOOGLE, "${SimpleConfig.serverUrl}$DEEPLINK_PATH")
        }
    }

    fun onClickFacebook() {
        status.load {
            oauthClient.signUp(OAuthServerName.FACEBOOK, "${SimpleConfig.serverUrl}$DEEPLINK_PATH")
        }
    }

    override fun onDeeplinkReceived(url: Url) {
        oauthClient.saveToken(url)
        finishSuccess()
    }

    private fun finishSuccess() {
        userRepo.invalidateUser()
        toast("success to sign up")
        goBack()
    }


    companion object {
        const val DEEPLINK_PATH = "${SimpleConfig.deeplinkPrePath}/signUp"
    }
}