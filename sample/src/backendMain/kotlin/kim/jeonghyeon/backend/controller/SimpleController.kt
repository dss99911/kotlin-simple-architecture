package kim.jeonghyeon.backend.controller

import kim.jeonghyeon.backend.log
import kim.jeonghyeon.sample.api.Item
import kim.jeonghyeon.sample.api.SimpleApi

class SimpleController : SimpleApi {
    override suspend fun getToken(
        int: Int,
        float: Float,
        double: Double,
        string: String,
        item: Item,
        nullable: String?
    ): String {
        log.info("")
        return "test"
    }

    override suspend fun submitPost(token: String, item: Item) {
        log.info("")
    }

    override suspend fun getWords(): String {
        TODO("Not yet implemented")
    }

    override suspend fun addWord(word: String) {
        TODO("Not yet implemented")
    }
}