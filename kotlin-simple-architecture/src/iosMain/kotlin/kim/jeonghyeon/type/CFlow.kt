package kim.jeonghyeon.type

import io.ktor.utils.io.core.Closeable
import kim.jeonghyeon.coroutine.observeTrial
import kim.jeonghyeon.mobile.dispatcherUI
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

fun <T> Flow<T>.asCFlow(): CFlow<T> = CFlow(this)
fun <T> ResourceFlow<T>.asCRFlow(): CRFlow<T> = CRFlow(this)

class CRFlow<T>(origin: ResourceFlow<T>) : CFlow<Resource<T>>(origin) {
    @InternalCoroutinesApi
    override fun watch(block: (Resource<T>) -> Unit): Closeable {
        val job = Job()

        block(Resource.Loading { job.cancel() })

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

open class CFlow<T>(private val origin: Flow<T>) : Flow<T> by origin {
    @InternalCoroutinesApi
    open fun watch(block: (T) -> Unit): Closeable {
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


fun <T> suspendToCRFlow(action: suspend () -> T): CRFlow<T> {
    return flow {
        observeTrial { retry ->
            try {
                Resource.Success(action())
            } catch (e: CancellationException) {
                //if cancel. then ignore it
                null
            } catch (e: ResourceError) {
                Resource.Error<T>(e) { retry() }
            } catch (e: Throwable) {
                Resource.Error<T>(UnknownResourceError(e)) { retry() }
            }?.let {
                emit(it)
            }
        }

    }.asCRFlow()
}