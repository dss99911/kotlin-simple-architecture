package kim.jeonghyeon.kotlinusecase.coroutine

import kotlinx.coroutines.*
import java.io.IOException

/**
 * top job receive exception and handle
 */
fun whoCatchException() {
    runBlocking {
        GlobalScope.launch(CoroutineExceptionHandler { _, exception ->
            println("0. Caught original $exception")
        }) {
            launch(CoroutineExceptionHandler { _, exception ->
                println("1. Caught original $exception")
            }) {
                launch(CoroutineExceptionHandler { _, exception ->
                    println("2. Caught original $exception")
                }) {
                    launch {
                        launch {
                            throw IOException()
                        }
                    }
                }
            }
            delay(1000)
        }.join()
    }

    println("completed")

    //0. Caught original java.io.IOException
    //completed
}

/**
 * if scope is same, but job is different, top-most job will handle the exception.
 */
fun whoCatchException2() {
    runBlocking {
        GlobalScope.launch(CoroutineExceptionHandler { _, exception ->
            println("0. Caught original $exception")
        }) {
            launch(Job() + CoroutineExceptionHandler { _, exception ->
                println("1. Caught original $exception")
            }) {
                launch(CoroutineExceptionHandler { _, exception ->
                    println("2. Caught original $exception")
                }) {
                    launch {
                        launch {
                            throw IOException()
                        }
                    }
                }
            }
            delay(1000)
        }.join()
    }

    println("completed")

    //1. Caught original java.io.IOException
    //completed
}

/**
 * with just Job, top-most job will handle the exception
 * otherwise, supervisorJob allow 2nd-high job to handle the exception
 * so that, prevent for all coroutine to be stopped by exception from child coroutine
 * if child coroutine doesn't contains exception handler. exception doesn't redirect to superviosrJob. but directly crash on the child coroutine
 */
fun supervisorJobTest() = runBlocking {
    val supervisor = SupervisorJob()
    with(CoroutineScope(coroutineContext + supervisor)) {
        // launch the first child -- its exception is ignored for this example (don't do this in practice!)
        val firstChild = launch(CoroutineExceptionHandler { _, _ -> }) {
            println("First child is failing")
            throw AssertionError("First child is cancelled")
        }
        // launch the second child
        val secondChild = launch {
            firstChild.join()
            // Cancellation of the first child is not propagated to the second child
            println("First child is cancelled: ${firstChild.isCancelled}, but second one is still active")
            try {
                delay(Long.MAX_VALUE)
            } finally {
                // But cancellation of the supervisor is propagated
                println("Second child is cancelled because supervisor is cancelled")
            }
        }
        // wait until the first child fails & completes
        firstChild.join()
        println("Cancelling supervisor")
        supervisor.cancel()
        secondChild.join()
    }

    //First child is failing
    //First child is cancelled: true, but second one is still active
    //Cancelling supervisor
    //Second child is cancelled because supervisor is cancelled
}

fun supervisorScopeTest() = runBlocking {
    val handler = CoroutineExceptionHandler { _, exception ->
        println("Caught $exception")
    }
    supervisorScope {
        val child = launch(handler) {
            println("Child throws an exception")
            throw AssertionError()
        }
        println("Scope is completing")
    }
    println("Scope is completed")
}