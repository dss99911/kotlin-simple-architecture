package kim.jeonghyeon.client

import kim.jeonghyeon.net.HttpResponseStore
import kim.jeonghyeon.type.Resource
import kim.jeonghyeon.type.ResourceError
import kim.jeonghyeon.type.UnknownResourceError
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

//todo if set loading inside launch, when same api is called two times at the same time, late call doesn't know if it's already started. but when I use Lazy, there was some issue on IOS. after version up. let's try again
fun <T> CoroutineScope.loadResource(
    state: ResourceFlow<T>? = null,
    work: suspend CoroutineScope.() -> T
) {
    //if error occurs in the async() before call await(), then crash occurs. this prevent the crash. but exeption occurs, so, exception will be catched in the getResource()
    //todo the mechanism seems changed, on ExceptionHandler, set Error.
    launch(CoroutineExceptionHandler { _, _ -> } + HttpResponseStore()) {
        state?.value = Resource.Loading { coroutineContext[Job]?.cancel() }

        getResource(work) {
            this@loadResource.loadResource(state, work)
        }?.also { state?.value = it }
    }
}

fun <T> CoroutineScope.loadResource(
    resourceState: ResourceFlow<T>? = null,
    statusState: StatusFlow? = null,
    work: suspend CoroutineScope.() -> T
) {
    //if error occurs in the async() before call await(), then crash occurs. this prevent the crash. but exeption occurs, so, exception will be catched in the getResource()
    launch(CoroutineExceptionHandler { _, _ -> } + HttpResponseStore()) {
        resourceState?.value = Resource.Loading { coroutineContext[Job]?.cancel() }
        statusState?.value = Resource.Loading { coroutineContext[Job]?.cancel() }
        getResource(work) {
            this@loadResource.loadResource(resourceState, statusState, work)
        }?.also {
            resourceState?.value = it
            statusState?.value = it
        }
    }
}

fun <T> CoroutineScope.loadDataAndStatus(
    data: DataFlow<T>,
    status: StatusFlow,
    work: suspend CoroutineScope.() -> T
) {
    //if error occurs in the async() before call await(), then crash occurs. this prevent the crash. but exeption occurs, so, exception will be catched in the getResource()
    launch(CoroutineExceptionHandler { _, _ -> } + HttpResponseStore()) {
        status.value = Resource.Loading { coroutineContext[Job]?.cancel() }
        getResource(work) {
            this@loadDataAndStatus.loadDataAndStatus(data, status, work)
        }?.also {
            it.onSuccess { data.value = it }
            status.value = it
        }
    }
}


fun <T, U> CoroutineScope.loadDataAndStatus(
    data: DataFlow<U>,
    status: StatusFlow,
    work: suspend CoroutineScope.() -> T,
    transform: suspend CoroutineScope.(Resource<T>) -> Resource<U>
) {
    //if error occurs in the async() before call await(), then crash occurs. this prevent the crash. but exeption occurs, so, exception will be catched in the getResource()
    launch(CoroutineExceptionHandler { _, _ -> } + HttpResponseStore()) {
        status.value = Resource.Loading { coroutineContext[Job]?.cancel() }
        getResource(work) {
            this@loadDataAndStatus.loadDataAndStatus(data, status, work, transform)
        }?.also {
            val transformed = transform(it)
            transformed.onSuccess { data.value = it }
            status.value = transformed
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
fun <T> CoroutineScope.loadFlow(
    resourceState: ResourceFlow<T>? = null,
    statusState: StatusFlow? = null,
    flow: Flow<Resource<T>>
) {
    //if error occurs in the async() before call await(), then crash occurs. this prevent the crash. but exeption occurs, so, exception will be catched in the getResource()
    launch(CoroutineExceptionHandler { _, _ -> } + HttpResponseStore()) {
        resourceState?.value = Resource.Loading { coroutineContext[Job]?.cancel() }
        statusState?.value = Resource.Loading { coroutineContext[Job]?.cancel() }
        flow.collect {
            resourceState?.value = it
            statusState?.value = it
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
fun <T> CoroutineScope.loadResourceFromFlow(
    data: DataFlow<T>,
    status: StatusFlow? = null,
    flow: Flow<Resource<T>>
) {
    //if error occurs in the async() before call await(), then crash occurs. this prevent the crash. but exeption occurs, so, exception will be catched in the getResource()
    launch(CoroutineExceptionHandler { _, _ -> } + HttpResponseStore()) {
        status?.value = Resource.Loading { coroutineContext[Job]?.cancel() }
        flow.collect {
            it.onSuccess { data.value = it }
            status?.value = it
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
fun <T> CoroutineScope.loadDataFromFlow(
    data: DataFlow<T>,
    status: StatusFlow? = null,
    flow: Flow<T>
) {
    //if error occurs in the async() before call await(), then crash occurs. this prevent the crash. but exeption occurs, so, exception will be catched in the getResource()
    launch(CoroutineExceptionHandler { _, _ -> } + HttpResponseStore()) {
        status?.value = Resource.Loading { coroutineContext[Job]?.cancel() }
        flow.collect {
            data.value = it
            status?.value = Resource.Success(it)
        }
    }
}


@OptIn(ExperimentalCoroutinesApi::class)
fun <T, U> CoroutineScope.loadResourceFromFlow(
    data: DataFlow<U>,
    status: StatusFlow? = null,
    flow: Flow<Resource<T>>,
    transform: suspend CoroutineScope.(Resource<T>) -> Resource<U>
) {
    //if error occurs in the async() before call await(), then crash occurs. this prevent the crash. but exeption occurs, so, exception will be catched in the getResource()
    launch(CoroutineExceptionHandler { _, _ -> } + HttpResponseStore()) {
        status?.value = Resource.Loading { coroutineContext[Job]?.cancel() }
        flow.collect {
            val transformed = transform(it)
            transformed.onSuccess { data.value = it }
            status?.value = transformed
        }
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