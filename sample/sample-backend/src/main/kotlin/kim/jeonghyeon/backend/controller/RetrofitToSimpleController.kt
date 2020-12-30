package kim.jeonghyeon.backend.controller

import kim.jeonghyeon.annotation.Body
import kim.jeonghyeon.annotation.Get
import kim.jeonghyeon.annotation.Path
import kim.jeonghyeon.annotation.Post
import kim.jeonghyeon.net.error.ApiErrorBody
import kim.jeonghyeon.net.error.errorApi
import kim.jeonghyeon.sample.api.*
import kotlin.random.Random
import kim.jeonghyeon.const.forTest
class RetrofitToSimpleController : RetrofitBackendApi {

    override suspend fun getValue(@Path("key") key: String): RetrofitResponseBodyBackend<RetrofitDataBackend> {
        if (Random.nextInt() % 2 == 0) {
            errorApi(ApiErrorBody.forTest)
        }
        return RetrofitResponseBodyBackend(RetrofitDataBackend("a", "B"))
    }

    override suspend fun setValue(@Path("key") key: String, @Body body: RetrofitRequestBodyBackend): RetrofitDataBackend {
        return RetrofitDataBackend("a", "B")
    }
}