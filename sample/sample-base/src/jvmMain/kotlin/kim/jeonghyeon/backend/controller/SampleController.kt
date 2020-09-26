package kim.jeonghyeon.backend.controller

import kim.jeonghyeon.backend.const.WORDS
import kim.jeonghyeon.backend.di.serviceLocatorBackend
import kim.jeonghyeon.const.post
import kim.jeonghyeon.net.HEADER_KEY
import kim.jeonghyeon.net.error.ApiErrorBody
import kim.jeonghyeon.net.error.errorApi
import kim.jeonghyeon.net.headers
import kim.jeonghyeon.pergist.Preference
import kim.jeonghyeon.sample.api.Post
import kim.jeonghyeon.sample.api.SampleApi
import kim.jeonghyeon.util.log
import kotlin.random.Random

class SampleController(val pref: Preference = serviceLocatorBackend.preference) : SampleApi {
    override suspend fun getToken(id: String, password: String): String {
        return "token"
    }

    override suspend fun submitPost(token: String, post: Post) {
        if (Random.nextBoolean()) {
            errorApi(ApiErrorBody.post)
        }
    }

    override suspend fun getWords(): List<String> {
        log.i("getWords")
        return pref.getString(pref.WORDS)?.split(",") ?: emptyList()
    }

    override suspend fun addWord(word: String) {
        val list = getWords().toMutableList().apply {
            add(word)
        }
        pref.setString(pref.WORDS, list.joinToString(","))
    }

    override suspend fun addWords(words: List<String>) {
        log.i("addWords")
        val list = getWords().toMutableList().apply {
            addAll(words)
        }
        pref.setString(pref.WORDS, list.joinToString(","))
    }

    override suspend fun removeWords() {
        log.i("removeWords")
        pref.setString(pref.WORDS, null)
    }

    override suspend fun getHeader(): String {
        return headers()[HEADER_KEY]!!
    }

    override suspend fun getAnnotation(
        id: String,
        action: String,
        auth: String
    ): Pair<Post, String> {
        return Pair(Post(1, "postValue"), "received $id, $action, $auth")
    }

    override suspend fun putAnnotation(id: String, post: Post) {
        log.i("id=$id, post=$post")
    }
}

