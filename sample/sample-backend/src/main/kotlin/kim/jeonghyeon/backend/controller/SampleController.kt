package kim.jeonghyeon.backend.controller

import kim.jeonghyeon.backend.const.KEY_WORDS
import kim.jeonghyeon.backend.di.serviceLocatorBackend
import kim.jeonghyeon.const.DeeplinkUrl
import kim.jeonghyeon.const.forTest
import kim.jeonghyeon.net.ControllerUtil.headers
import kim.jeonghyeon.net.DeeplinkInfo
import kim.jeonghyeon.net.HEADER_KEY
import kim.jeonghyeon.net.error.ApiErrorBody
import kim.jeonghyeon.net.error.errorApi
import kim.jeonghyeon.net.errorDeeplink
import kim.jeonghyeon.pergist.Preference
import kim.jeonghyeon.sample.api.AnnotationAction
import kim.jeonghyeon.sample.api.AnnotationObject
import kim.jeonghyeon.sample.api.SampleApi
import kim.jeonghyeon.util.log
import kotlin.random.Random

class SampleController(val pref: Preference = serviceLocatorBackend.preference) : SampleApi {
    var number = 0

    override suspend fun getWords(): List<String> {
        return pref.getString(Preference.KEY_WORDS)?.split(",") ?: emptyList()
    }

    override suspend fun getWordsOfKeyword(keyword: String): List<String> {
        val list = pref.getString(Preference.KEY_WORDS)?.split(",") ?: emptyList()
        return list.filter {
            it.contains(keyword)
        }
    }

    override suspend fun addWord(word: String) {
        val list = getWords().toMutableList().apply {
            add(word)
        }
        pref.setString(Preference.KEY_WORDS, list.joinToString(","))
    }

    override suspend fun addWords(words: List<String>) {
        log.i("addWords $words")
        val list = getWords().toMutableList().apply {
            addAll(words)
        }
        pref.setString(Preference.KEY_WORDS, list.joinToString(","))
    }

    override suspend fun removeWords() {
        log.i("removeWords")
        pref.setString(Preference.KEY_WORDS, null)
    }

    override suspend fun getHeader(): String {
        return headers()[HEADER_KEY]!!
    }

    override suspend fun getAnnotation(
        key: String,
        action: AnnotationAction,
        someHeader: String
    ): AnnotationObject {
        log.i("key = $key")
        return AnnotationObject(key, Pair("aa", someHeader), action)
    }

    override suspend fun putAnnotation(key: String, body: AnnotationObject): AnnotationObject =
        body.copy(key = key)


    override suspend fun testDeeplink() {
        errorDeeplink(DeeplinkInfo(DeeplinkUrl.DEEPLINK_PATH_SIGN_UP, "Please Sign up for testing deeplink"))
    }

    override suspend fun getIncreasedNumber(): Int {
        return ++number
    }

    override suspend fun getSuccess() {
        //do nothing
    }

    override suspend fun getError() {
        errorApi(ApiErrorBody.forTest)
    }

    override suspend fun getRandomError(per: Int) {
        if (Random.nextInt() % per == 0) {
            errorApi(ApiErrorBody.forTest)
        }
    }

    override suspend fun repeat(text: String, times: Int): String {
        return text.repeat(times)
    }

    override suspend fun minus(firstNumber: Int, secondNumber: Int): Int =
        firstNumber - secondNumber
}

