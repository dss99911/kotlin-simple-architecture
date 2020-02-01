package kim.jeonghyeon.kotlinusecase.lambda

fun main() {
    val a = A()
    val lam: () -> Int

    //we can choose method conditionally
    if (true) {
        lam = a::a
    } else {
        lam = a::b
    }

    println(lam())
}

class A {
    fun a(): Int = 1
    fun b(): Int = 2
}