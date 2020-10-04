package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.sample.di.serviceLocator
import kim.jeonghyeon.sample.repository.UserRepository

class UserViewModel(val userRepo: UserRepository) : SampleViewModel() {
    override val signInRequired: Boolean = true

    //todo required for ios to create instance, currently kotlin doesn't support predefined parameter
    // if it's supported, remove this
    constructor() : this(serviceLocator.userRepository)

    val user by add { userRepo.userDetail.toDataFlow(initStatus) }

    fun onClickLogOut() = status.load {
        userRepo.signOut()
    }
}