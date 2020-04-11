package kim.jeonghyeon.sample.apicall.simple

import kim.jeonghyeon.sample.apicall.Item
import kim.jeonghyeon.sample.apicall.PostRequestBody
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SimpleApi {
    suspend fun getToken(int: Int = 1, float: Float = 1f, double: Double = 1.0, string: String = "dd", item: Item = Item(1, "d"), nullable: String? = null): Item
    suspend fun submitPost(token: String, item: Item): Item?
}