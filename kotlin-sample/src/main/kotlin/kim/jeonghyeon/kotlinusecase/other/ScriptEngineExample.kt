@file:Suppress("RedundantExplicitType", "unused")

package kim.jeonghyeon.kotlinusecase.other

import javax.script.ScriptEngineManager

class ScriptEngineExample {
    fun engine() {
        val engine = ScriptEngineManager().getEngineByExtension("kts")!!
        engine.eval("val x = 3")
        println(engine.eval("x + 2"))  // Prints out 5
    }
}