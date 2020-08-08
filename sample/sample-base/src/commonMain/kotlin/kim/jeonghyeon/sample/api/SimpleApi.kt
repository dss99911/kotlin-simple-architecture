package kim.jeonghyeon.sample.api

import kim.jeonghyeon.annotation.*
import kotlinx.serialization.Serializable

@Api
interface SimpleApi {
    suspend fun getToken(id: String, password: String): String

    suspend fun submitPost(token: String, post: Post)


    suspend fun getWords(): List<String>

    suspend fun addWord(word: String)

    suspend fun getHeader(): String

    @Get("annotation/{id}")
    suspend fun getAnnotation(@Path("id") id: String, @Query("action") action: String, @Header("auth") auth: String): Pair<Post, String>

    @Put("annotation/{id}")
    suspend fun putAnnotation(@Path("id") id: String, @Body post: Post)
}


@Serializable
data class Post(val id: Int, val name: String)