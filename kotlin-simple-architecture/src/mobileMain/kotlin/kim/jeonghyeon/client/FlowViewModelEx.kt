package kim.jeonghyeon.client

import kim.jeonghyeon.type.Resource
import kim.jeonghyeon.type.ResourceError
import kim.jeonghyeon.type.Status
import kim.jeonghyeon.type.UnknownResourceError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

fun <T> flowViewModel(): MutableSharedFlow<T> = MutableSharedFlow(1, 0, BufferOverflow.DROP_OLDEST)
fun <T> flowViewModel(initialValue: T): MutableSharedFlow<T> = MutableSharedFlow<T>(1, 0, BufferOverflow.DROP_OLDEST)
    .apply {
        tryEmit(initialValue)
    }

/**
 * for empty value.
 */
fun MutableSharedFlow<Unit>.call() {
    tryEmit(Unit)
}
val <T> SharedFlow<T>.value: T
    get(): T = replayCache[0]

val <T> SharedFlow<T>.valueOrNull get(): T? = replayCache.getOrNull(0)

var <T> MutableSharedFlow<T>.value: T
    get(): T = replayCache[0]
    set(value) { tryEmit(value) }



/**
 * handle exception, and make resource
 * able to retry
 * if retry several times, previous retry get cancelled
 */
fun <T> Flow<T>.collectResource(scope: CoroutineScope, action: suspend (value: Resource<T>) -> Unit): Job {
    var job: Job? = null
    return map<T, Resource<T>> {
        Resource.Success(it) {
            job?.cancel()
            job = collectResource(scope, action)//todo synchronize
        }
    }.catch {
        emit(Resource.Error(if (it is ResourceError) it else UnknownResourceError(it)) {
            scope.launch {
                job?.cancel()
                job = collectResource(scope, action)//todo synchronize
            }

        })
    }.onStart {
        action(Resource.Loading(cancel = {
            job?.cancel()
        }) {
            job?.cancel()
            job = collectResource(scope, action)//todo synchronize
        })
    }.onEach(action)
        .launchIn(scope)
        .also { job = it }

}

/**
 * There are two ways to deliver value to other flow
 * 1. subscribe : when flow is changed, observe it, and reflect on the subscriber
 * 2. assign : when flow is changed, assign to subscriber flows
 *
 * the flow can be cold flow.
 * and if same data should be assigned on several subscribers,
 * if you use 'subscribe' way, you have to use shareIn operator. and also have to consider exception.
 * so, this method provide simple way to assign values.
 *
 */
fun <T> Flow<T>.assign(scope: CoroutineScope, result: MutableSharedFlow<Resource<T>>) {
    collectResource(scope) {
        result.emit(it)
    }
}

fun <T> Flow<T>.assign(scope: CoroutineScope, data: MutableSharedFlow<T>, status: MutableSharedFlow<Status>) {
    collectResource(scope) {
        if (it is Resource.Success) {
            data.emit(it.value)
        }
        status.emit(it)
    }
}

fun <T> Flow<T>.assignWithStatus(scope: CoroutineScope, resource: MutableSharedFlow<Resource<T>>, status: MutableSharedFlow<Status>) {
    collectResource(scope) {
        resource.emit(it)
        status.emit(it)
    }
}

/**
 * for single flow without transforming.
 */
fun <T> MutableSharedFlow<Resource<T>>.loadResource(scope: CoroutineScope, action: suspend () -> T) {
    flowSingle(action).assign(scope, this)
}

fun <T> MutableSharedFlow<Resource<T>>.loadResourceWithStatus(scope: CoroutineScope, status: MutableSharedFlow<Status>, action: suspend () -> T) {
    flowSingle(action).assignWithStatus(scope, this, status)
}

/**
 * for single flow without transforming.
 */
fun <T> MutableSharedFlow<T>.loadResource(scope: CoroutineScope, status: MutableSharedFlow<Status>, action: suspend () -> T) {
    flowSingle(action).assign(scope, this, status)
}