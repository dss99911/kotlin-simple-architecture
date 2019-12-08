package kim.jeonghyeon.androidlibrary.testutil

import kim.jeonghyeon.androidlibrary.deprecated.thread.AppExecutors
import java.util.concurrent.Executor

class InstantAppExecutors : AppExecutors(instant, instant, instant) {
    companion object {
        private val instant = Executor { it.run() }
    }
}