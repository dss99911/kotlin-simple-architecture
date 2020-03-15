package kim.jeonghyeon.sample.apicall.coroutine

import kim.jeonghyeon.sample.apicall.PostRequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CoroutineApi {
    @GET("token")
    suspend fun getToken(): String

    @POST("post")
    suspend fun submitPost(@Body body: PostRequestBody)

    @GET("status/{mockResult}")
    suspend fun getStatus(@Path("mockResult") mockResult: Boolean): Boolean

    @GET("error")
    suspend fun getError(): String
}