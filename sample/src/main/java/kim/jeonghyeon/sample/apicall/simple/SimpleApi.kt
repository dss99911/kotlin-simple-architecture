package kim.jeonghyeon.sample.apicall.simple

import kim.jeonghyeon.sample.apicall.Item

interface SimpleApi {
    suspend fun getToken(int: Int = 1, float: Float = 1f, double: Double = 1.0, string: String = "dd", item: Item = Item(1, "d"), nullable: String? = null): Item
    suspend fun submitPost(token: String, item: Item)
}