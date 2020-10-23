package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.sample.di.serviceLocator
import kim.jeonghyeon.sample.repository.UserRepository

class UserViewModel(val userRepo: UserRepository = serviceLocator.userRepository) : ModelViewModel() {

    //todo [KSA-48] support localization on kotlin side
    override val title: String = "User"

    override val signInRequired: Boolean = true

    val user by add { userRepo.userDetail.toDataFlow(initStatus) }

    fun onClickLogOut() = status.load {
        userRepo.signOut()
    }
}