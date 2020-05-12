package kim.jeonghyeon.common.net

import kim.jeonghyeon.simplearchitecture.plugin.Api

@Api
interface ApiJvm {
    suspend fun aa()
    suspend fun a(a: HashMap<String, Int>)
}