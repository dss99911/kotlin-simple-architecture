package kim.jeonghyeon.simplearchitecture.plugin

import kim.jeonghyeon.simplearchitecture.plugin.te.TestReturn

@Api
interface ApiTest {
    suspend fun action1()
    suspend fun action2(name: HashMap<String, Int>)
    suspend fun action3(name: String): Map.Entry<String, Int>
    suspend fun action4(aa: TestReturn.AA): TestReturn?
    suspend fun action5()
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


