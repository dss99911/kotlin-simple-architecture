package kim.jeonghyeon.sample.api

import kim.jeonghyeon.annotation.*
import kotlinx.serialization.Serializable

@Api("/")
interface RetrofitBackendApi {
    @Get("retrofit/{key}/value")
    suspend fun getValue(@Path("key") key: String): RetrofitResponseBodyBackend<RetrofitDataBackend>

    @Post("retrofit/{key}/value")
    suspend fun setValue(@Path("key") key: String, @Body body: RetrofitRequestBodyBackend): RetrofitDataBackend
}

@Serializable
data class RetrofitRequestBodyBackend(val value: String)
@Serializable
data class RetrofitResponseBodyBackend<T>(val data: T)
@Serializable
data class RetrofitDataBackend(val name: String, val id: String)