package kim.jeonghyeon.sample.apicall.chaining

import kim.jeonghyeon.androidlibrary.architecture.livedata.LiveResource
import kim.jeonghyeon.sample.apicall.PostRequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ChainingApi {
    @GET("token")
    fun getToken(): LiveResource<String>

    @POST("post")
    fun submitPost(@Body body: PostRequestBody): LiveResource<Unit>
}