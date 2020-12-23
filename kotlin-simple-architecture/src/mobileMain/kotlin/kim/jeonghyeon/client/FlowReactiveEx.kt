package kim.jeonghyeon.client

import kim.jeonghyeon.type.Resource
import kim.jeonghyeon.type.Status
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlin.experimental.ExperimentalTypeInference


fun <T, R> Flow<T>.mapInIdle(scope: CoroutineScope, transformData: suspend (value: T) -> R): Flow<R> =
    mapWithJob(scope, FlowJobPolicy.IN_IDLE, transformData)

fun <T, R> Flow<T>.mapCancelRunning(scope: CoroutineScope, transformData: suspend (value: T) -> R): Flow<R> =
    mapWithJob(scope, FlowJobPolicy.CANCEL_RUNNING, transformData)

@OptIn(ExperimentalTypeInference::class)
fun <T, R> Flow<T>.transformInIdle(scope: CoroutineScope, @BuilderInference transformData: suspend FlowCollector<R>.(value: T) -> Unit): Flow<R> =
    transformWithJob(scope, FlowJobPolicy.IN_IDLE, transformData)

@OptIn(ExperimentalTypeInference::class)
fun <T, R> Flow<T>.transformCancelRunning(scope: CoroutineScope, @BuilderInference transformData: suspend FlowCollector<R>.(value: T) -> Unit): Flow<R> =
    transformWithJob(scope, FlowJobPolicy.CANCEL_RUNNING, transformData)


/**
 * convert to resource
 * able to retry.
 * shared, so, @receiver is collected only one time.
 * allow multiple emit. SUSPEND
 * retry collect all emits.
 */
fun <T> Flow<T>.toResource(scope: CoroutineScope): MutableSharedFlow<Resource<T>> {
    val result = flowViewModel<Resource<T>>()
    result.onActive(scope) {
        if (it) {
            collectResource(scope) {
                result.emit(it)
            }
            currentCoroutineContext().cancel()
        }
    }
    return result
}

fun <T> Flow<T>.toStatus(scope: CoroutineScope): MutableSharedFlow<Status> {
    val result = flowViewModel<Status>()
    result.onActive(scope) {
        if (it) {
            collectResource(scope) {
                result.emit(it)
            }
            currentCoroutineContext().cancel()
        }
    }
    return result
}

/**
 * - 1 cache
 * - assign data, status separately
 */
fun <T> Flow<T>.toData(scope: CoroutineScope, status: MutableSharedFlow<Status>? = null): MutableSharedFlow<T> {
    val result = flowViewModel<T>()
    result.onActive(scope) {
        if (it) {
            collectResource(scope) {
                if (it is Resource.Success) {
                    result.emit(it.value)
                }
                status?.emit(it)
            }
            currentCoroutineContext().cancel()
        }

    }
    return result
}

/**
 *
 * transform with [FlowJobPolicy]
 * each emit affect on other emit.
 * @param scope transformData is processed on this scope.
 *
 */
@OptIn(ExperimentalTypeInference::class)
private fun <T, R> Flow<T>.transformWithJob(scope: CoroutineScope, jobPolicy: FlowJobPolicy, @BuilderInference transformData: suspend FlowCollector<R>.(value: T) -> Unit): Flow<R> {
    val result = MutableSharedFlow<Any?>()
    return result.onActive(scope) {
        if (it) {
            var job: Job? = null
            scope.launch {
                catch {
                    result.emit(ExceptionOnFlow(it))
                }.collect {
                    if (jobPolicy == FlowJobPolicy.CANCEL_RUNNING) {
                        job?.cancel()
                    }

                    if (jobPolicy != FlowJobPolicy.IN_IDLE || job?.isActive != true) {
                        job = scope.launch {
                            try {
                                object : FlowCollector<R> {
                                    override suspend fun emit(value: R) {
                                        result.emit(value)
                                    }
                                }.transformData(it)
                            } catch (e: Throwable) {
                                result.emit(ExceptionOnFlow(e))
                            }
                        }
                    }
                }
            }
            currentCoroutineContext().cancel()
        }

    }.map {
        if (it is ExceptionOnFlow) {
            throw it.exception
        }
        it as R
    }
}

/**
 * refer to the doc [transformWithJob]
 */
private fun <T, R> Flow<T>.mapWithJob(scope: CoroutineScope, jobPolicy: FlowJobPolicy, transformData: suspend (value: T) -> R): Flow<R> {
    return transformWithJob(scope, jobPolicy) {
        emit(transformData(it))
    }
}



private enum class FlowJobPolicy {
    /**
     * if transforming is busy. then upstream is ignored
     * if transforming is not busy but downstream is busy. upstream is accepted.
     */
    IN_IDLE,
    /**
     * if transforming is busy when upstream comes, then previous transforming get cancelled.
     * if transforming is not busy but downstream is busy, then downstream is not cancelled.
     */
    CANCEL_RUNNING
}

private class ExceptionOnFlow(val exception: Throwable)