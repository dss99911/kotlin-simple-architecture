package kim.jeonghyeon.client

import kim.jeonghyeon.net.HttpResponseStore
import kim.jeonghyeon.type.Resource
import kim.jeonghyeon.type.ResourceError
import kim.jeonghyeon.type.UnknownResourceError
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect

fun <T> CoroutineScope.loadResource(
    state: ResourceStateFlow<T>? = null,
    work: suspend CoroutineScope.() -> T
) {
    //if error occurs in the async() before call await(), then crash occurs. this prevent the crash. but exeption occurs, so, exception will be catched in the getResource()
    launch(CoroutineExceptionHandler { _, _ -> } + HttpResponseStore(), CoroutineStart.LAZY) {
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
    launch(CoroutineExceptionHandler { _, _ -> } + HttpResponseStore(), CoroutineStart.LAZY) {
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
    launch(CoroutineExceptionHandler { _, _ -> } + HttpResponseStore(), CoroutineStart.LAZY) {
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

fun <T, U> CoroutineScope.loadDataAndStatus(
    data: MutableStateFlow<U>,
    status: StatusStateFlow,
    work: suspend CoroutineScope.() -> T,
    transform: suspend CoroutineScope.(Resource<T>) -> Resource<U>
) {
    //if error occurs in the async() before call await(), then crash occurs. this prevent the crash. but exeption occurs, so, exception will be catched in the getResource()
    launch(CoroutineExceptionHandler { _, _ -> } + HttpResponseStore(), CoroutineStart.LAZY) {
        getResource(work) {
            this@loadDataAndStatus.loadDataAndStatus(data, status, work, transform)
        }?.also {
            val transformed = transform(it)
            transformed.onSuccess { data.value = it }
            status.value = transformed
        }
    }.also {
        status.value = Resource.Loading { it.cancel() }
        it.start()
    }
}

fun <T> CoroutineScope.loadFlow(
    resourceState: ResourceStateFlow<T>? = null,
    statusState: StatusStateFlow? = null,
    flow: () -> Flow<T>
) {
    //if error occurs in the async() before call await(), then crash occurs. this prevent the crash. but exeption occurs, so, exception will be catched in the getResource()
    launch(CoroutineExceptionHandler { _, _ -> } + HttpResponseStore(), CoroutineStart.LAZY) {
        getResource(flow(),
            onResult = {
                resourceState?.value = it
                statusState?.value = it
            }) {
            this@loadFlow.loadFlow(resourceState, statusState, flow)
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
    flow: () -> Flow<T>
) {
    //if error occurs in the async() before call await(), then crash occurs. this prevent the crash. but exeption occurs, so, exception will be catched in the getResource()
    launch(CoroutineExceptionHandler { _, _ -> } + HttpResponseStore(), CoroutineStart.LAZY) {
        getResource(flow(),
            onResult = {
                it.onSuccess { data.value = it }
                status.value = it
            }) {
            this@loadDataFromFlow.loadDataFromFlow(data, status, flow)
        }
    }.also {
        status.value = Resource.Loading { it.cancel() }
        it.start()
    }
}

//todo check and remove previous function
@OptIn(ExperimentalCoroutinesApi::class)
fun <T> CoroutineScope.loadFlow2(
    resourceState: ResourceStateFlow<T>? = null,
    statusState: StatusStateFlow? = null,
    flow: Flow<Resource<T>>
) {
    //if error occurs in the async() before call await(), then crash occurs. this prevent the crash. but exeption occurs, so, exception will be catched in the getResource()
    launch(CoroutineExceptionHandler { _, _ -> } + HttpResponseStore(), CoroutineStart.LAZY) {
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

@OptIn(ExperimentalCoroutinesApi::class)
fun <T> CoroutineScope.loadDataFromFlow(
    data: MutableStateFlow<T>,
    status: StatusStateFlow? = null,
    flow: Flow<Resource<T>>
) {
    //if error occurs in the async() before call await(), then crash occurs. this prevent the crash. but exeption occurs, so, exception will be catched in the getResource()
    launch(CoroutineExceptionHandler { _, _ -> } + HttpResponseStore(), CoroutineStart.LAZY) {
        flow.collect {
            it.onSuccess { data.value = it }
            status?.value = it
        }
    }.also {
        status?.value = Resource.Loading { it.cancel() }
        it.start()
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
fun <T, U> CoroutineScope.loadDataFromFlow(
    data: MutableStateFlow<U>,
    status: StatusStateFlow? = null,
    flow: Flow<Resource<T>>,
    transform: suspend CoroutineScope.(Resource<T>) -> Resource<U>
) {
    //if error occurs in the async() before call await(), then crash occurs. this prevent the crash. but exeption occurs, so, exception will be catched in the getResource()
    launch(CoroutineExceptionHandler { _, _ -> } + HttpResponseStore(), CoroutineStart.LAZY) {
        flow.collect {
            val transformed = transform(it)
            transformed.onSuccess { data.value = it }
            status?.value = transformed
        }
    }.also {
        status?.value = Resource.Loading { it.cancel() }
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
} catch (e: Throwable) {
    Resource.Error(UnknownResourceError(e), retry = retry)
}


private suspend fun <T> CoroutineScope.getResource(
    flow: Flow<T>,
    onResult: (Resource<T>) -> Unit,
    retry: () -> Unit
) {
    return try {
        flow.collect {
            onResult(Resource.Success(it))
        }
    } catch (e: CancellationException) {
        //if cancel. then ignore it
    } catch (e: ResourceError) {
        onResult(Resource.Error(e, retry = retry))
    } catch (e: Throwable) {
        onResult(Resource.Error(UnknownResourceError(e), retry = retry))
    }
}
