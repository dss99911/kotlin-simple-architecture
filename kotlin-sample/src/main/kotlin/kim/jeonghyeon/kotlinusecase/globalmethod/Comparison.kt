@file:Suppress("RedundantExplicitType", "unused")

package kim.jeonghyeon.kotlinusecase.globalmethod

class Comparison {
    fun minMax() {
        val list1 = listOf("a", "b")
        val list2 = listOf("x", "y", "z")
        val minSize = minOf(list1.size, list2.size)
        val longestList = maxOf(list1, list2, compareBy { it.size })
    }
}