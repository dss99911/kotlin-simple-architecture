package kim.jeonghyeon.kotlinusecase.coroutine

import kotlinx.coroutines.*


/**
 * coroutineScope() 을 쓰면, 내부에서 크래시가 발생시 해당 스코프가 취소되고, 크래시를 발생시킨다
어떤 처리를 위해서, 임시로 생성되는 스코프로, 도중에 오작동시, 알리기 위해서 크래시가 발생.
GlobalScope는 해당 스코프내에서 corountine에서 크래시가 발생하면, 해당 corountine만 중단되고, 외부에 영향은 없음. 영향을 주려면, async{}를 await()해야 함
 */
fun corountineScopeTest() {
    runBlocking {
        coroutineScope {
            async {
                println("error")
                error("error occur")
            }
        }
    }
}

/**
 * crash will be delivered to await()
 *
 */
private fun crashOnAsync() {
    val async = GlobalScope.async {
        println("error")
        error("error occur")
    }
}


/**
 * here 8 worker thread is running
 * and thread is blocked by Thread.sleep(). so, other coroutine didn't work.
 */
private fun launchBlocked() {
    val deferred = (1..1_000).map { n ->
        GlobalScope.launch {
            println(Thread.currentThread())
            Thread.sleep(10000)

        }
    }
    runBlocking {
        delay(5000)

    }
}
