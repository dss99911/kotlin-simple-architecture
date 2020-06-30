package kim.jeonghyeon.sample.api

import kim.jeonghyeon.annotation.Api
import kotlinx.serialization.Serializable

@Api
interface SimpleApi {
    suspend fun getToken(id: String, password: String): String

    suspend fun submitPost(token: String, post: Post)


    suspend fun getWords(): List<String>

    suspend fun addWord(word: String)
}

@Serializable
data class Post(val id: Int, val name: String)