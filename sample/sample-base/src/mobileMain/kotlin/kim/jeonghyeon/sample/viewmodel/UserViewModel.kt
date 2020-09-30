package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.net.error.ApiError
import kim.jeonghyeon.net.error.ApiErrorBody
import kim.jeonghyeon.sample.api.SerializableUserDetail
import kim.jeonghyeon.sample.di.serviceLocator
import kim.jeonghyeon.sample.repository.UserRepository
import kim.jeonghyeon.type.Resource

class UserViewModel(val userRepo: UserRepository) : SampleViewModel() {
    override val signInRequired: Boolean = true

    //todo required for ios to create instance, currently kotlin doesn't support predefined parameter
    // if it's supported, remove this
    constructor(): this(serviceLocator.userRepository)

    val user = dataFlow<SerializableUserDetail?>(null)

    override fun onInit() {
        user.load(initStatus, userRepo.userDetail)
    }

    fun onClickLogOut() = status.load {
        userRepo.signOut()
    }
}