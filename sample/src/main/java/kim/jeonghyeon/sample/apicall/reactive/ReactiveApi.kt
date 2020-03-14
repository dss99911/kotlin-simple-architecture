package kim.jeonghyeon.sample.apicall.reactive

import io.reactivex.Flowable
import kim.jeonghyeon.sample.apicall.PostRequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ReactiveApi {
    @GET("token")
    fun getToken(): Flowable<String>

    @POST("post")
    fun submitPost(@Body body: PostRequestBody): Flowable<Unit>
}