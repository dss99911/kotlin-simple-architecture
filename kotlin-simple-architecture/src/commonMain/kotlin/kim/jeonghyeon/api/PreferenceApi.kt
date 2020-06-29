package kim.jeonghyeon.api

import kim.jeonghyeon.annotation.Api

@Api
interface PreferenceApi {
    suspend fun getString(key: String): String?
    suspend fun setString(key: String, value: String?)
}