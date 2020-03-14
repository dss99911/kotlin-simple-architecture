package kim.jeonghyeon.sample.apicall.callback

import kim.jeonghyeon.sample.apicall.PostRequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CallbackApi {
    @GET("token")
    fun getToken(): Call<String>

    @POST("post")
    fun submitPost(@Body body: PostRequestBody): Call<Unit>
}