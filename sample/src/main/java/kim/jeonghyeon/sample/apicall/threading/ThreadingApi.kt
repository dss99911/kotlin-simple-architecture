package kim.jeonghyeon.sample.apicall.threading

import kim.jeonghyeon.sample.apicall.PostRequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ThreadingApi {
    @GET("token")
    fun getToken(): String

    @POST("post")
    fun submitPost(@Body body: PostRequestBody): Any//Retrofit doesn't support Unit.
}