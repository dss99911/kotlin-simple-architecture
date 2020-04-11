package kim.jeonghyeon.backend.controller

import kim.jeonghyeon.backend.log
import kim.jeonghyeon.common.net.api.Item
import kim.jeonghyeon.common.net.api.SimpleApi

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