package kim.jeonghyeon.kotlinusecase.delegate

class Test : IA by A(), IB by B {
    fun test() {
        b()
        d()
    }
}

interface IA {
    val a: String
    fun b()
}


class A : IA {
    override val a: String
        get() = "aa"

    override fun b() {
        println(a)
    }
}

interface IB {
    val c: String
    fun d()
}

object B : IB {
    override val c: String
        get() = "bb"

    override fun d() {
        println(c)
    }
}