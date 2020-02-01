@file:Suppress("RedundantExplicitType", "unused")

package kim.jeonghyeon.kotlinusecase.other

class CallableReferenceExample {

    /**
     * numberRegex::matches
     */
    fun test() {
        val numberRegex = "\\d+".toRegex()
        val numbers = listOf("abc", "123", "456").filter(numberRegex::matches)

    }

    /**
     * String::isEmpty
     */
    fun test2() {
        val result = "asdf".takeUnless(String::isEmpty)
    }
}
