package kim.jeonghyeon.kotlinusecase.coroutine

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


/**
 * on delay(), the thread is not blocked, but can be used by other corountine
 */
private fun launchDelay() {
    val deferred = (1..1_000).map { n ->
        GlobalScope.launch {
            println(Thread.currentThread())
            delay(10000)

        }
    }
    runBlocking {
        delay(5000)

    }
}

/**
 * yield the dispatcher to other coroutine
 */
fun yield() {
    runBlocking {
        launch {
            println(1)
            yield()
            println(2)
        }
        launch {
            println(3)
        }
    }
    // 1 -> 3 -> 2

}
