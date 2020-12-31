package kim.jeonghyeon.sample.api

import kim.jeonghyeon.annotation.Api
import kim.jeonghyeon.annotation.Authenticate
import kotlinx.serialization.Serializable

@Api
@Authenticate
interface UserApi {
    suspend fun getUser(): SerializableUserDetail
}

@Serializable
data class SerializableUserDetail(val id: String? = null, val name: String)