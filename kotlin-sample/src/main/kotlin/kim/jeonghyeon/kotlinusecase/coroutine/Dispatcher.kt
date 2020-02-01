package kim.jeonghyeon.kotlinusecase.coroutine

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * if doesn't specify Dispatcher, it follows current scope
 */
fun defaultDispatcher() {
    runBlocking {

        //it follow runBlocking's scope
        launch {
            println(Thread.currentThread())
        }.join()
    }
    //Thread[kim.jeonghyeon.file.main,5,kim.jeonghyeon.file.main]
}