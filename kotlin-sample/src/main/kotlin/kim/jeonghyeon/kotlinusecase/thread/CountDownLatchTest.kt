package kim.jeonghyeon.kotlinusecase.thread

import java.util.concurrent.CountDownLatch

class CountDownLatchTest {
    fun a() {
        val countDownLatch = CountDownLatch(3)
        b(countDownLatch)//call on other thread
        countDownLatch.await()// wait until 3times of countDown occur
    }


    fun b(countDownLatch: CountDownLatch) {
        countDownLatch.countDown()
        countDownLatch.countDown()
        countDownLatch.countDown()
    }
}