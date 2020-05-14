package kim.jeonghyeon.jvm.net

import kim.jeonghyeon.simplearchitecture.plugin.Api

@Api
interface ApiJvm {
    suspend fun aa()
    suspend fun a(a: HashMap<String, Int>)
}