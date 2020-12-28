package kim.jeonghyeon.client

import kim.jeonghyeon.annotation.SimpleArchInternal
import kim.jeonghyeon.type.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlin.experimental.ExperimentalTypeInference


/**
 * interface is not recognized on swift.
 * so added wrapper
 *
 * plus, refer the viewModel. so, swift side code looks simpler. but it may not good approach.
 * also, ViewModelFlow can't be shared on multiple viewModel. consider this one more time when developing a viewModel refer another viewModel
 */
class ViewModelFlow<T>(viewModel: BaseViewModel, val flow: MutableSharedFlow<T>) : MutableSharedFlow<T> {
    val viewModelWeakReference: WeakReference<BaseViewModel> = WeakReference(viewModel)
    /**
     * used on Swift
     */
    fun asValue(): T? {
        val viewModel = viewModelWeakReference.get()?:return valueOrNull

        if (viewModel.flowSet.value.contains(this)) {
            return valueOrNull
        }

        viewModel.flowSet.value = (viewModel.flowSet.value + this)
        viewModel.scope.launch {
            flow.collect {
                viewModel.changeCount.value ++
            }
        }

        return valueOrNull
    }

    @InternalCoroutinesApi
    override suspend fun collect(collector: FlowCollector<T>) {
        flow.collect(collector)
    }

    override val subscriptionCount: StateFlow<Int>
        get() = flow.subscriptionCount

    override suspend fun emit(value: T) {
        flow.emit(value)
    }

    @ExperimentalCoroutinesApi
    override fun resetReplayCache() {
        flow.resetReplayCache()
    }

    override fun tryEmit(value: T): Boolean {
        return flow.tryEmit(value)
    }

    override val replayCache: List<T>
        get() = flow.replayCache

    var value: T
        get(): T = replayCache[0]
        set(value) { tryEmit(value)}

    val valueOrNull get(): T? = replayCache.getOrNull(0)
}

/**
 * for empty value.
 */
fun ViewModelFlow<Unit>.call() {
    tryEmit(Unit)
}


fun <T> flowSingle(action: suspend () -> T): Flow<T> = flow {
    emit(action())
}

/**
 * - sharedFlow doesn't throw exception to downstream
 * - if exception occurs, all subscriber is disconnected. at that time, reset cache, so that, when retry by collecting again. upstream will be collected again.
 */
fun <T> Flow<T>.toShare(scope: CoroutineScope): Flow<T> {
    val job = atomic<Job?>(null)
    val flow = MutableSharedFlow<Resource<T>>(1, 0, BufferOverflow.DROP_OLDEST)
    flow.onActive(scope) {
        if (it) {
            job.value = scope.launch {
                this@toShare
                    .map<T, Resource<T>> { Resource.Success(it) }
                    .catch { emit(Resource.Error(if (it is ResourceError) it else UnknownResourceError(it))) }
                    .collect { flow.emit(it) }
            }
        } else {
            job.value?.cancel()
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