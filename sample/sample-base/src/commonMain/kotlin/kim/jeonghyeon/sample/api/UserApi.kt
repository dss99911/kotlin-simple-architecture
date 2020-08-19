package kim.jeonghyeon.sample.api

import kim.jeonghyeon.annotation.Api
import kim.jeonghyeon.annotation.Authenticate
import kim.jeonghyeon.sample.UserDetail
import kotlinx.serialization.Serializable

@Api
@Authenticate
interface UserApi {
    suspend fun getUser(): SerializableUserDetail
}

@Serializable
data class SerializableUserDetail(val id: String?, val name: String)

fun UserDetail.serializable(): SerializableUserDetail = SerializableUserDetail(id, name)