package kim.jeonghyeon.backend.user

import kim.jeonghyeon.backend.auth.USER_EXTRA_KEY_NAME
import kim.jeonghyeon.backend.auth.USER_EXTRA_KEY_SIGN_ID
import kim.jeonghyeon.net.ControllerUtil
import kim.jeonghyeon.net.ControllerUtilArchitecture.userExtra
import kim.jeonghyeon.sample.api.SerializableUserDetail
import kim.jeonghyeon.sample.api.UserApi

class UserController : UserApi {
    override suspend fun getUser(): SerializableUserDetail {
        return SerializableUserDetail(
            userExtra(USER_EXTRA_KEY_SIGN_ID)!!,
            userExtra(USER_EXTRA_KEY_NAME)!!
        )
    }
}