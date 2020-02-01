@file:Suppress("RedundantExplicitType", "unused")

package kim.jeonghyeon.kotlinusecase.delegate

fun localLazy() {
    val answer by lazy {
        println("Calculating the answer...")
        42
    }
}