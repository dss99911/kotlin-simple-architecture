package kim.jeonghyeon

import kotlin.reflect.full.functions
import kotlin.reflect.jvm.isAccessible

fun main() {
    val annotations = A::class.annotations
    val functions = UserController::class.functions
    functions.forEach {
        println(it.name + ":" + it.annotations)
    }
    A::class.constructors.forEach {
        it.isAccessible = true
        it.annotations.forEach {
            //            it.
        }
    }
}

annotation class Get(val path: String)
annotation class Post(val path: String)

@Get("aa")
class A

fun a() {
    A::class.annotations
}

class UserController : UserApi {
    @Get("/user2")
    override fun getUser(id: String) {

    }
}

interface UserApi {
    @Get("/user")
    fun getUser(id: String)
}