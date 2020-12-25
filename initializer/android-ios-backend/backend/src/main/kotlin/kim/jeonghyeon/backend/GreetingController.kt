package kim.jeonghyeon.backend

import kim.jeonghyeon.base.GreetingApi

class GreetingController : GreetingApi {
    override suspend fun greeting(): String = "Welcome to Backend"
}