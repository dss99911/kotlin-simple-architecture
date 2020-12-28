package kim.jeonghyeon.api

import kim.jeonghyeon.annotation.Api
import kim.jeonghyeon.annotation.Authenticate

@Suppress("RUNTIME_ANNOTATION_NOT_SUPPORTED")
@Api
interface PreferenceApi {
    @Authenticate
    suspend fun getStringPerUser(key: String): String?
    @Authenticate
    suspend fun setStringPerUser(key: String, value: String?)

    /**
     * this is for whole user
     */
    suspend fun getString(key: String): String?

    //setString for all is not supported. as it can affect to other user.
    // so, set string on backend only.
}