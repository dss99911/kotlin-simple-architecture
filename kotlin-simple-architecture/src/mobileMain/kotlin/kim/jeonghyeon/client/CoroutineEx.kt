package kim.jeonghyeon.client

import kim.jeonghyeon.net.HttpResponseStore
import kim.jeonghyeon.type.Resource
import kim.jeonghyeon.type.ResourceError
import kim.jeonghyeon.type.Status
import kim.jeonghyeon.type.UnknownResourceError
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow

//todo if set loading inside launch, when same api is called two times at the same time, late call doesn't know if it's already started. but when I use Lazy, there was some issue on IOS. after version up. let's try again
fun <T> CoroutineScope.loadResource(
    state: ViewModelFlow<Resource<T>>? = null,
    work: suspend CoroutineScope.() -> T
) {
    //if error occurs in the async() before call await(), then crash occurs. this prevent the crash. but exeption occurs, so, exception will be catched in the getResource()
    //todo the mechanism seems changed, on ExceptionHandler, set Error.
    launch(CoroutineExceptionHandler { _, _ -> } + HttpResponseStore()) {
        performAndSet(this, work, null, state) {
            this@loadResource.loadResource(state, work)
        }
    }
}


fun <T> CoroutineScope.loadResource(
    resourceState: ViewModelFlow<Resource<T>>? = null,
    statusState: ViewModelFlow<Status>? = null,
    work: suspend CoroutineScope.() -> T
) {
    //if error occurs in the async() before call await(), then crash occurs. this prevent the crash. but exeption occurs, so, exception will be catched in the getResource()
    launch(CoroutineExceptionHandler { _, _ -> } + HttpResponseStore()) {
        performAndSet(this, work, null, resourceState, statusState) {
            this@loadResource.loadResource(resourceState, statusState, work)
        }
    }
}

fun <T> CoroutineScope.loadDataAndStatus(
    data: ViewModelFlow<T>,
    status: ViewModelFlow<Status>,
    work: suspend CoroutineScope.() -> T
) {
    //if error occurs in the async() before call await(), then crash occurs. this prevent the crash. but exeption occurs, so, exception will be catched in the getResource()
    launch(CoroutineExceptionHandler { _, _ -> } + HttpResponseStore()) {
        performAndSet(this, work, data, null, status) {
            this@loadDataAndStatus.loadDataAndStatus(data, status, work)
        }
    }
}


fun <T, U> CoroutineScope.loadDataAndStatus(
    data: ViewModelFlow<U>,
    status: ViewModelFlow<Status>,
    work: suspend CoroutineScope.() -> T,
    transform: suspend CoroutineScope.(Resource<T>) -> Resource<U>
) {
    //if error occurs in the async() before call await(), then crash occurs. this prevent the crash. but exeption occurs, so, exception will be catched in the getResource()
    launch(CoroutineExceptionHandler { _, _ -> } + HttpResponseStore()) {
        performAndSet(this, work, data, null, status, transform) {
            this@loadDataAndStatus.loadDataAndStatus(data, status, work, transform)
        }
    }
}

private suspend fun <T> performAndSet(
    scope: CoroutineScope,
    work: suspend CoroutineScope.() -> T,
    dataFlow: ViewModelFlow<T>? = null,
    resourceFlow: ViewModelFlow<Resource<T>>? = null,
    statusFlow: ViewModelFlow<Status>? = null,
    retry: () -> Unit
) {
    resourceFlow?.value = Resource.Loading(cancel = { scope.coroutineContext[Job]?.cancel() }, retryData = retry)
    statusFlow?.value = Resource.Loading(cancel = { scope.coroutineContext[Job]?.cancel() }, retryData = retry)
    scope.getResource(work, retry)?.also {
        resourceFlow?.value = it
        statusFlow?.value = it
        it.onSuccess {
            dataFlow?.value = it
        }
    }
}

private suspend fun <T, U> performAndSet(
    scope: CoroutineScope,
    work: suspend CoroutineScope.() -> T,
    dataFlow: ViewModelFlow<U>? = null,
    resourceFlow: ViewModelFlow<Resource<U>>? = null,
    statusFlow: ViewModelFlow<Status>? = null,
    transform: suspend CoroutineScope.(Resource<T>) -> Resource<U>,
    retry: () -> Unit
) {
    resourceFlow?.value = Resource.Loading(cancel = { scope.coroutineContext[Job]?.cancel() }, retryData = retry)
    statusFlow?.value = Resource.Loading(cancel = { scope.coroutineContext[Job]?.cancel() }, retryData = retry)
    scope.getResource(work, retry)?.also {
        val transformed = scope.transform(it)
        resourceFlow?.value = transformed
        statusFlow?.value = transformed
        transformed.onSuccess {
            dataFlow?.value = it
        }
    }
}

private suspend fun <T> CoroutineScope.getResource(
    action: suspend CoroutineScope.() -> T,
    retry: () -> Unit
): Resource<T>? = try {
    Resource.Success(action(this), retryData = retry)
} catch (e: CancellationException) {
    //if cancel. then ignore it
    null
} catch (e: ResourceError) {
    Resource.Error(e, retryData = retry)
} catch (e: Throwable) {
    Resource.Error(UnknownResourceError(e), retryData = retry)
}


