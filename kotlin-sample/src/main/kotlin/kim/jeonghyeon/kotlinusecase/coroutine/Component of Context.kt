package kim.jeonghyeon.kotlinusecase.coroutine

import kotlinx.coroutines.*

/**
 * Job : manage coroutines,exception is depending on this
 * Dispatchers : which thread it will be processed
 * Exception Handler : how to process exceptions
 * Coroutine name
 */
fun components() {
    val exceptionHandler = CoroutineExceptionHandler { _, _ -> println() }
    GlobalScope.launch(
        Job()
                + Dispatchers.Default
                + exceptionHandler
                + CoroutineName("test")
    ) {

    }
}