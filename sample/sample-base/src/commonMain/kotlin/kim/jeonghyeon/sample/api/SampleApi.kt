package kim.jeonghyeon.sample.api

import kim.jeonghyeon.annotation.*
import kotlinx.serialization.Serializable

@Api
interface SampleApi {
    @Authenticate
    suspend fun getWords(): List<String>

    /**
     * get words contains [keyword]
     */
    @Authenticate
    suspend fun getWordsOfKeyword(keyword: String): List<String>

    @Authenticate
    suspend fun addWord(word: String)

    @Authenticate
    suspend fun addWords(words: List<String>)

    @Authenticate
    suspend fun removeWords()

    suspend fun getHeader(): String

    suspend fun getIncreasedNumber(): Int

    suspend fun getSuccess()

    suspend fun getError()

    suspend fun getRandomError(per: Int)

    suspend fun repeat(text: String, times: Int): String

    suspend fun minus(firstNumber: Int, secondNumber: Int): Int

    @Get("annotation/{key}")
    suspend fun getAnnotation(@Path("key") key: String, @Query("action") action: AnnotationAction, @Header("someHeader") someHeader: String): AnnotationObject

    @Put("annotation/{key}")
    suspend fun putAnnotation(@Path("key") key: String, @Body body: AnnotationObject): AnnotationObject

    suspend fun testDeeplink()
}

@Serializable
data class AnnotationObject(val key: String, val data: Pair<String, String>, val action: AnnotationAction)


enum class AnnotationAction {
    QUERY,
    INSERT,
    UPDATE,
    DELETE
}