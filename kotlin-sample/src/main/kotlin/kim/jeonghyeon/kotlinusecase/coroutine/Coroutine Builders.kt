package kim.jeonghyeon.kotlinusecase.coroutine

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking

fun returnData() {
    runBlocking {
        coroutineScope {
            15
        }
    }.also {
        println(it)
    }

    //15
}