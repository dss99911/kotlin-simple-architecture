package kim.jeonghyeon.androidlibrary.testutil

import kim.jeonghyeon.androidlibrary.architecture.thread.AppExecutors
import java.util.concurrent.Executor

class InstantAppExecutors : AppExecutors(instant, instant, instant) {
    companion object {
        private val instant = Executor { it.run() }
    }
}