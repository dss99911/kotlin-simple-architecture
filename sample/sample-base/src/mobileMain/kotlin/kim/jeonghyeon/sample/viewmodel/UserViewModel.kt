package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.client.viewModelFlow
import kim.jeonghyeon.sample.di.serviceLocator
import kim.jeonghyeon.sample.repository.UserRepository
import kim.jeonghyeon.type.Status
import kotlinx.coroutines.flow.MutableSharedFlow

class UserViewModel(val userRepo: UserRepository = serviceLocator.userRepository) : ModelViewModel() {

    //todo [KSA-48] support localization on kotlin side
    override val title: String = "User"
    override val signInRequired: Boolean = true

    val user = userRepo.userDetail.toData(initStatus)

    fun onClickLogOut() = status.load {
        userRepo.signOut()
    }
}

// TODO reactive way.
//class UserViewModel2(val userRepo: UserRepository = serviceLocator.userRepository) : ModelViewModel() {
//
//    //todo [KSA-48] support localization on kotlin side
//    override val title: String = "User"
//    override val signInRequired: Boolean = true
//
//    val user by add { userRepo.userDetail.toData(initStatus) }
//    val click = viewModelFlow<Unit>()
//
//    override val status: MutableSharedFlow<Status> by add {
//        click.mapInIdle {
//            userRepo.signOut()
//        }.toStatus()
//    }
//}