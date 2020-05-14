package kim.jeonghyeon.common.net

import kim.jeonghyeon.simplearchitecture.plugin.Api

@Api
interface ApiTestIos {
    suspend fun aa()
    suspend fun a(a: HashMap<String, Int>)
}