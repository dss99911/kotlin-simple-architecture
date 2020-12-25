package kim.jeonghyeon.base

import kim.jeonghyeon.annotation.Api

@Api
interface GreetingApi {
    suspend fun greeting(): String
}