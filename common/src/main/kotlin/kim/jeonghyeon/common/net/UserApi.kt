package kim.jeonghyeon.common.net

import kim.jeonghyeon.simplearchitecture.plugin.Api

@Api
interface UserApi {
    suspend fun action1()
    suspend fun action2(name: String)
    suspend fun action3(name: String)
    suspend fun a(a: HashMap<String, Int>)
}