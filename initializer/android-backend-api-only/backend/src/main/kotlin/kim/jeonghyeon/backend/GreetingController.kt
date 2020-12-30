package kim.jeonghyeon.backend

import kim.jeonghyeon.base.GreetingApi
import kim.jeonghyeon.base.Data

class GreetingController : GreetingApi {
    override suspend fun greeting(): Data = Data("Welcome to Backend")
}