package kim.jeonghyeon.backend.controller

import kim.jeonghyeon.POST_ERROR
import kim.jeonghyeon.api.preference
import kim.jeonghyeon.common.net.error.ApiError
import kim.jeonghyeon.common.net.error.ApiErrorBody
import kim.jeonghyeon.common.net.error.ApiErrorCode
import kim.jeonghyeon.pergist.Preference
import kim.jeonghyeon.sample.api.Post
import kim.jeonghyeon.sample.api.SimpleApi
import kotlin.random.Random

class SimpleController : SimpleApi {
    override suspend fun getToken(id: String, password: String): String {
        return "token"
    }

    override suspend fun submitPost(token: String, post: Post) {
        if (Random.nextBoolean()) {
            throw ApiError(ApiErrorBody(ApiErrorCode.POST_ERROR, "post error"))
        }
    }

    override suspend fun getWords(): List<String> {
        return preference.getString(preference.WORDS)?.split(",") ?: emptyList()
    }

    override suspend fun addWord(word: String) {
        val list = getWords().toMutableList().apply {
            add(word)
        }
        preference.setString(preference.WORDS, list.joinToString(","))
    }
}

val Preference.WORDS get() = "KEY_WORDS"