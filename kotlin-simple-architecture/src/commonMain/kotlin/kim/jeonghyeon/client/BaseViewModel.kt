package kim.jeonghyeon.client


import kim.jeonghyeon.type.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect

typealias StatusStateFlow = MutableStateFlow<Status>
typealias ResourceStateFlow<T> = MutableStateFlow<Resource<T>>

fun StatusStateFlow(status: Status = Resource.Start): StatusStateFlow = MutableStateFlow(status)
fun <T> ResourceStateFlow(resource: Resource<T> = Resource.Start): ResourceStateFlow<T> = MutableStateFlow(resource)

open class BaseViewModel {

    val initStatus: StatusStateFlow = StatusStateFlow()
    val status: StatusStateFlow = StatusStateFlow()

    val isInitialized: AtomicReference<Boolean> = atomic(false)

    val scope: ViewModelScope by lazy { ViewModelScope() }

    //todo is it possible to use init {}
    open fun onInitialized() {
    }

    //@CallSuper
    open fun onCleared() {
        scope.close()
    }

    fun <T> ResourceStateFlow<T>.load(work: suspend CoroutineScope.() -> T) {
        scope.loadResource(this, work)
    }

    fun <T> ResourceStateFlow<T>.loadWithStatus(status: StatusStateFlow, work: suspend CoroutineScope.() -> T) {
        scope.loadResource(this, status, work)
    }

    fun <T> MutableStateFlow<T>.load(status: StatusStateFlow, work: suspend CoroutineScope.() -> T) {
        scope.loadDataAndStatus(this, status, work)
    }

    fun <T> ResourceStateFlow<T>.loadInIdle(work: suspend CoroutineScope.() -> T) {
        if (value.isLoading()) {
            return
        }
        scope.loadResource(this, status, work)
    }

    fun <T> loadInIdle(work: suspend CoroutineScope.() -> T) {
        status.loadInIdle(work)
    }

    fun <T> ResourceStateFlow<T>.load(flow: Flow<Resource<T>>) {
        scope.loadFlow(this, null, flow)
    }

    fun <T> MutableStateFlow<T>.load(status: StatusStateFlow, flow: ResourceFlow<T>) {
        scope.loadDataFromFlow(this, status, flow)
    }

    fun <T, U> MutableStateFlow<T>.withSource(
        source: MutableStateFlow<U>,
        onCollect: MutableStateFlow<T>.(U) -> Unit
    ): MutableStateFlow<T> {
        scope.launch {
            source.collect {
                onCollect(this@withSource, it)
            }
        }
        return this
    }

}

fun <T> CoroutineScope.loadResource(
    state: ResourceStateFlow<T>? = null,
    work: suspend CoroutineScope.() -> T
) {
    //if error occurs in the async() before call await(), then crash occurs. this prevent the crash. but exeption occurs, so, exception will be catched in the getResource()
    launch(CoroutineExceptionHandler { _, _ -> }, CoroutineStart.LAZY) {
        getResource(work) {
            this@loadResource.loadResource(state, work)
        }?.also { state?.value = it }
    }.also {
        state?.value = Resource.Loading { it.cancel() }
        it.start()
    }
}

fun <T> CoroutineScope.loadResource(
    resourceState: ResourceStateFlow<T>? = null,
    statusState: StatusStateFlow? = null,
    work: suspend CoroutineScope.() -> T
) {
    //if error occurs in the async() before call await(), then crash occurs. this prevent the crash. but exeption occurs, so, exception will be catched in the getResource()
    launch(CoroutineExceptionHandler { _, _ -> }, CoroutineStart.LAZY) {
        getResource(work) {
            this@loadResource.loadResource(resourceState, statusState, work)
        }?.also {
            resourceState?.value = it
            statusState?.value = it
        }
    }.also {
        resourceState?.value = Resource.Loading { it.cancel() }
        statusState?.value = Resource.Loading { it.cancel() }
        it.start()
    }
}

fun <T> CoroutineScope.loadDataAndStatus(
    data: MutableStateFlow<T>,
    status: StatusStateFlow,
    work: suspend CoroutineScope.() -> T
) {
    //if error occurs in the async() before call await(), then crash occurs. this prevent the crash. but exeption occurs, so, exception will be catched in the getResource()
    launch(CoroutineExceptionHandler { _, _ -> }, CoroutineStart.LAZY) {
        getResource(work) {
            this@loadDataAndStatus.loadDataAndStatus(data, status, work)
        }?.also {
            it.onSuccess { data.value = it }
            status.value = it
        }
    }.also {
        status.value = Resource.Loading { it.cancel() }
        it.start()
    }
}

fun <T> CoroutineScope.loadFlow(
    resourceState: ResourceStateFlow<T>? = null,
    statusState: StatusStateFlow? = null,
    flow: ResourceFlow<T>
) {
    //if error occurs in the async() before call await(), then crash occurs. this prevent the crash. but exeption occurs, so, exception will be catched in the getResource()
    launch(CoroutineExceptionHandler { _, _ -> }, CoroutineStart.LAZY) {
        flow.collect {
            resourceState?.value = it
            statusState?.value = it
        }
    }.also {
        resourceState?.value = Resource.Loading { it.cancel() }
        statusState?.value = Resource.Loading { it.cancel() }
        it.start()
    }
}

fun <T> CoroutineScope.loadDataFromFlow(
    data: MutableStateFlow<T>,
    status: StatusStateFlow,
    flow: ResourceFlow<T>
) {
    //if error occurs in the async() before call await(), then crash occurs. this prevent the crash. but exeption occurs, so, exception will be catched in the getResource()
    launch(CoroutineExceptionHandler { _, _ -> }, CoroutineStart.LAZY) {
        flow.collect {
            it.onSuccess { data.value = it }
            status.value = it
        }
    }.also {
        status.value = Resource.Loading { it.cancel() }
        it.start()
    }
}

private suspend fun <T> CoroutineScope.getResource(
    action: suspend CoroutineScope.() -> T,
    retry: () -> Unit
): Resource<T>? = try {
    Resource.Success(action(this))
} catch (e: CancellationException) {
    //if cancel. then ignore it
    null
} catch (e: ResourceError) {
    Resource.Error(e, retry = retry)
} catch (e: Exception) {
    Resource.Error(UnknownResourceError(e), retry = retry)
}
