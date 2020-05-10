package kim.jeonghyeon.simplearchitecture.plugin

import kim.jeonghyeon.simplearchitecture.plugin.te.AA
import kim.jeonghyeon.simplearchitecture.plugin.te.TestReturn

@Api
interface ApiTest {
    suspend fun action1()
    fun action2(name: HashMap<String, Int>)
    fun action3(name: String): Map.Entry<String, Int>
    fun action4(aa: AA): TestReturn?
}

@Api
abstract class ApiTest2 {
    fun aa() {

    }

    abstract fun bb()

    @Api
    class ApiTest3 {
        fun bb() {

        }
    }
}


