package kim.jeonghyeon.mobile

import kim.jeonghyeon.simplearchitecture.plugin.Api

@Api
interface ApiTestMobile {
    suspend fun aaa()
    suspend fun a(a: HashMap<String, Int>)
}