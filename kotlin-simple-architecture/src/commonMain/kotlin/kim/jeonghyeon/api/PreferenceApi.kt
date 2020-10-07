package kim.jeonghyeon.api

import kim.jeonghyeon.annotation.Api

@Suppress("RUNTIME_ANNOTATION_NOT_SUPPORTED")
@Api
interface PreferenceApi {
    suspend fun getString(key: String): String?
    suspend fun setString(key: String, value: String?)
}