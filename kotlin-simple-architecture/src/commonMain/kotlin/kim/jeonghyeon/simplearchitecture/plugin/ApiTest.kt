package kim.jeonghyeon.simplearchitecture.plugin

import kim.jeonghyeon.simplearchitecture.plugin.te.TestReturn

@Api
interface ApiTest {
    suspend fun action1()
    suspend fun action2()
    suspend fun action3(name: Map.Entry<String, Int>)
    suspend fun action4(aa: TestReturn.AA?)
    suspend fun action5()
    suspend fun a(a: HashMap<String, Int>)
}

@Api
class ApiTest2 {
    fun aa() {
    }

    @Api
    class ApiTest3 {
        fun bb() {

        }
    }
}


