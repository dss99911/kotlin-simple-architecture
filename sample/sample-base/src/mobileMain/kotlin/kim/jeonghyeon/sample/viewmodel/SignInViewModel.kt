package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.net.error.ApiError
import kim.jeonghyeon.net.error.ApiErrorBody
import kim.jeonghyeon.sample.api.SerializableUserDetail
import kim.jeonghyeon.sample.di.serviceLocator
import kim.jeonghyeon.type.Resource

class SignInViewModel(/*val userRepo: UserRepository*/) : BaseViewModel() {

    //todo required for ios to create instance, currently kotlin doesn't support predefined parameter
    // if it's supported, remove this
//    constructor(): this(serviceLocator.userRepository)

    val inputId = dataFlow("")
    val inputPassword = dataFlow("")
    val user = dataFlow<SerializableUserDetail?>(null)

    override fun onInitialized() {
        getUserDetail()
    }

    fun onClickLogOut() = status.load {
        //todo after this fixed https://youtrack.jetbrains.com/issue/KTOR-973
        // use constructor parameter
        serviceLocator.userRepository.signOut()
    }

    fun onClickSignIn() = status.load {
        //todo after this fixed https://youtrack.jetbrains.com/issue/KTOR-973
        // use constructor parameter
        serviceLocator.userRepository.signIn(inputId.value, inputPassword.value)
    }

    //todo after this fixed https://youtrack.jetbrains.com/issue/KTOR-973
    // use constructor parameter
    private fun getUserDetail() = user.load(initStatus, serviceLocator.userRepository.userDetail) {
        //if unauthorized error, show login page.
        if (it.isErrorOf<ApiError>()) {
            if (it.errorOf<ApiError>().code == ApiErrorBody.Unauthorized.code) {
                return@load Resource.Success<SerializableUserDetail?>(null)
            }
        }
        it
    }
}