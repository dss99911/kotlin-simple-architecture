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
        performAndSet(this, work, null, state) {
            this@loadResource.loadResource(state, work)
        }
    }
}



fun <T> CoroutineScope.loadResource(
    resourceState: ResourceFlow<T>? = null,
    statusState: StatusFlow? = null,
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
    data: DataFlow<T>,
    status: StatusFlow,
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
    data: DataFlow<U>,
    status: StatusFlow,
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
    dataFlow: DataFlow<T>? = null,
    resourceFlow: ResourceFlow<T>? = null,
    statusFlow: StatusFlow? = null,
    retry: () -> Unit
) {
    resourceFlow?.setValue(Resource.Loading(cancel = { scope.coroutineContext[Job]?.cancel() }, retryData = retry))
    statusFlow?.setValue(Resource.Loading(cancel = { scope.coroutineContext[Job]?.cancel() }, retryData = retry))
    scope.getResource(work, retry)?.also {
        resourceFlow?.setValue(it)
        statusFlow?.setValue(it)
        it.onSuccess {
            dataFlow?.setValue(it)
        }
    }
}

private suspend fun <T, U> performAndSet(
    scope: CoroutineScope,
    work: suspend CoroutineScope.() -> T,
    dataFlow: DataFlow<U>? = null,
    resourceFlow: ResourceFlow<U>? = null,
    statusFlow: StatusFlow? = null,
    transform: suspend CoroutineScope.(Resource<T>) -> Resource<U>,
    retry: () -> Unit
) {
    resourceFlow?.setValue(Resource.Loading(cancel = { scope.coroutineContext[Job]?.cancel() }, retryData = retry))
    statusFlow?.setValue(Resource.Loading(cancel = { scope.coroutineContext[Job]?.cancel() }, retryData = retry))
    scope.getResource(work, retry)?.also {
        val transformed = scope.transform(it)
        resourceFlow?.setValue(transformed)
        statusFlow?.setValue(transformed)
        transformed.onSuccess {
            dataFlow?.setValue(it)
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


