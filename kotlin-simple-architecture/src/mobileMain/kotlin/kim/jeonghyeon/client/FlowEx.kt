package kim.jeonghyeon.client

import kim.jeonghyeon.type.Resource
import kim.jeonghyeon.type.ResourceError
import kim.jeonghyeon.type.Status
import kim.jeonghyeon.type.UnknownResourceError
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlin.experimental.ExperimentalTypeInference


fun <T> flowSingle(action: suspend () -> T): Flow<T> = flow {
    emit(action())
}

/**
 * - sharedFlow doesn't throw exception to downstream
 * - if exception occurs, all subscriber is disconnected. at that time, reset cache, so that, when retry by collecting again. upstream will be collected again.
 */
fun <T> Flow<T>.toShare(scope: CoroutineScope): Flow<T> {
    var job: Job? = null
    val flow = MutableSharedFlow<Resource<T>>(1, 0, BufferOverflow.DROP_OLDEST)
    flow.onActive(scope) {
        if (it) {
            job = scope.launch {
                this@toShare
                    .map<T, Resource<T>> { Resource.Success(it) }
                    .catch { emit(Resource.Error(if (it is ResourceError) it else UnknownResourceError(it))) }
                    .collect { flow.emit(it) }
            }
        } else {
            job?.cancel()
            if (flow.replayCache.getOrNull(0)?.isError() == true) {
                flow.resetReplayCache()
            }
        }
    }

    return flow.map {
        if (it is Resource.Success) {
            it.value
        } else {
            throw it.error()
        }
    }
}


@OptIn(ExperimentalTypeInference::class)
fun <T> shareFlow(
    scope: CoroutineScope,
    @BuilderInference block: suspend FlowCollector<T>.() -> Unit
): Flow<T> = flow(block).toShare(scope)


fun <T> MutableSharedFlow<T>.onActive(scope: CoroutineScope, action: suspend (active: Boolean) -> Unit): MutableSharedFlow<T> {
    subscriptionCount
        .map { count -> count > 0 }
        .distinctUntilChanged()
        .onEach {
            action(it)
        }.launchIn(scope)
    return this
}

@OptIn(ExperimentalTypeInference::class)
inline fun <T, U> MutableSharedFlow<T>.withSource(
    scope: CoroutineScope,
    source: Flow<U>,
    @BuilderInference crossinline transform: suspend FlowCollector<T>.(value: U) -> Unit
): MutableSharedFlow<T> = onActive(scope) {
    if (it) {
        scope.launch {
            emitAll(source.transform(transform))
        }
        currentCoroutineContext().cancel()
    }

}

fun <T> MutableSharedFlow<T>.withSource(
    scope: CoroutineScope,
    source: Flow<T>
): MutableSharedFlow<T> = withSource(scope, source) {
    emit(it)
}