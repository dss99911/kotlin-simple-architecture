package kim.jeonghyeon.backend.user

import io.ktor.sessions.clear
import io.ktor.sessions.get
import kim.jeonghyeon.backend.di.serviceLocator
import kim.jeonghyeon.backend.net.sessions
import kim.jeonghyeon.sample.User
import kim.jeonghyeon.sample.UserQueries
import kim.jeonghyeon.sample.api.UserApi
import kim.jeonghyeon.sample.api.UserDetail

class UserController : UserApi {
    override suspend fun getUser(): UserDetail {
        return sessions().get<User>()!!.let {
            UserDetail(it.id, it.name)
        }
    }
}