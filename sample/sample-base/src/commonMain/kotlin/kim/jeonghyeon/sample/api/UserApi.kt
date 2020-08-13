package kim.jeonghyeon.sample.api

import kim.jeonghyeon.annotation.Api
import kim.jeonghyeon.annotation.Authenticate
import kotlinx.serialization.Serializable

@Api
@Authenticate
interface UserApi {
    suspend fun getUser(): UserDetail
}

@Serializable
data class UserDetail(val id: String, val name: String)