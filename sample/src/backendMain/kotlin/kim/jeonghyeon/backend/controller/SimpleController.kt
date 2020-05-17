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
    ): Item {
        log.info("")
        return Item(10, "test")
    }

    override suspend fun submitPost(token: String, item: Item) {
        log.info("")
    }
}