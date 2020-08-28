package kim.jeonghyeon.sample.repository

import kim.jeonghyeon.client.ResourceStateFlow
import kim.jeonghyeon.coroutine.resourceFlow
import kim.jeonghyeon.sample.api.SerializableUserDetail
import kim.jeonghyeon.sample.api.UserApi
import kim.jeonghyeon.sample.di.serviceLocator
import kim.jeonghyeon.type.Resource
import kotlinx.coroutines.flow.Flow

class UserRepository(
    private val userApi: UserApi = serviceLocator.userApi
) {
    private var retry: ()-> Unit = {}

    //as it's singleton, it keeps data in memory until processor terminated
    val userDetail = resourceFlow {
        retry = it
        emit(userApi.getUser())
    }

    //when userdetail is changed, update it.
    //todo there is possiblity that server change user data. or client mistakingly doesn't invalidate after user detail changed.
    // so consider to use web socket
    // and also consider use graphQL as this approach need to call api one more time causing user to wait longer time
    fun invalidateUser() {
        retry()
    }
}