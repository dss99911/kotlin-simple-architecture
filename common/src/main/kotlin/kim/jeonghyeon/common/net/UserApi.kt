package kim.jeonghyeon.common.net

import kim.jeonghyeon.simplearchitecture.plugin.Api
import kim.jeonghyeon.simplearchitecture.plugin.ApiTestImpl

@Api
interface UserApi {
    fun action1()
    fun action2(name: String)
    fun action3(name: String): Int
}