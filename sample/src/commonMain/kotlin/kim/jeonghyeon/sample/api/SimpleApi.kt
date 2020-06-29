package kim.jeonghyeon.sample.api

import kim.jeonghyeon.annotation.Api
import kotlinx.serialization.Serializable

@Api
interface SimpleApi {
    suspend fun getToken(
        int: Int = 1,
        float: Float = 1f,
        double: Double = 1.0,
        string: String = "dd",
        item: Item = Item(
            1,
            "d"
        ),
        nullable: String? = null
    ): String

    suspend fun submitPost(token: String, item: Item)


    suspend fun getWords(): String

    suspend fun addWord(word: String)
}

@Serializable
data class Item(val id: Int, val name: String)