package kim.jeonghyeon.sample.api

import kim.jeonghyeon.annotation.Api
import kim.jeonghyeon.annotation.Authenticate
import kotlinx.serialization.Serializable

@Api
@Authenticate
interface UserApi {
    suspend fun getUser(): SerializableUserDetail
}

//todo use sqldelight's user class https://github.com/JetBrains/kotlinconf-app/blob/dfcb77eea0ccf8044878fd417f11925d63faabc0/common/src/commonMain/kotlin/org/jetbrains/kotlinconf/GMTDateSerializer.kt
@Serializable
data class SerializableUserDetail(val id: String?, val name: String)