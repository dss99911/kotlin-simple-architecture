package kim.jeonghyeon.type

import io.ktor.utils.io.core.Closeable
import kim.jeonghyeon.mobile.dispatcherUI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

fun <T> Flow<T>.wrap(): CFlow<T> = CFlow(this)

class CFlow<T>(private val origin: Flow<T>) : Flow<T> by origin {
    @InternalCoroutinesApi
    fun watch(block: (T) -> Unit): Closeable {
        val job = Job()

        onEach {
            block(it)
        }.launchIn(CoroutineScope(dispatcherUI() + job))

        return object : Closeable {
            override fun close() {
                job.cancel()
            }
        }
    }
}