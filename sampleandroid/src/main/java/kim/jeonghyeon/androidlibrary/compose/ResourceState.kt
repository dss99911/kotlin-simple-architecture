package kim.jeonghyeon.androidlibrary.compose

import androidx.compose.FrameManager
import androidx.compose.MutableState
import androidx.compose.mutableStateOf
import kim.jeonghyeon.type.Resource
import kim.jeonghyeon.type.ResourceError
import kim.jeonghyeon.type.Status
import kim.jeonghyeon.type.UnknownResourceError
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

/**
 * if value is null. doesn't show
 */

//todo change to Resource<T> not null type
typealias ResourceState<T> = MutableState<Resource<T>>
typealias StatusState = MutableState<Status>

fun <T> resourceStateOf(): ResourceState<T> = mutableStateOf(Resource.Start)

val <T> ResourceState<T>.isSuccess: Boolean get() = value.isSuccess()

fun <T> ResourceState<T>.data(): T = value.data()
fun <T> ResourceState<T>.dataOrNull(): T? = value.dataOrNull()
fun <T> ResourceState<T>.setSuccess(data: T) {
    value = Resource.Success(data)
}


fun statusStateOf(): StatusState = mutableStateOf(Resource.Start)

fun <T> CoroutineScope.loadResource(
    state: ResourceState<T>? = null,
    work: suspend CoroutineScope.() -> T
) {
    //if error occurs in the async() before call await(), then crash occurs. this prevent the crash. but exeption occurs, so, exception will be catched in the getResource()
    launch(CoroutineExceptionHandler { _, _ -> }, CoroutineStart.LAZY) {
        getResource(work) {
            this@loadResource.loadResource(state, work)
        }?.also { state?.value = it }
    }.also {
        state?.value = Resource.Loading(it)
        it.start()
    }
}

fun <T> CoroutineScope.loadResource(
    resourceState: ResourceState<T>? = null,
    statusState: StatusState? = null,
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
        resourceState?.value = Resource.Loading(it)
        statusState?.value = Resource.Loading(it)
        it.start()
    }
}

fun <T> CoroutineScope.loadFlow(
    resourceState: ResourceState<T>? = null,
    statusState: StatusState? = null,
    flow: Flow<Resource<T>>
) {
    //if error occurs in the async() before call await(), then crash occurs. this prevent the crash. but exeption occurs, so, exception will be catched in the getResource()
    launch(CoroutineExceptionHandler { _, _ -> }, CoroutineStart.LAZY) {
        flow.collect {
            FrameManager.framed {
                resourceState?.value = it
                statusState?.value = it
            }
        }
    }.also {
        resourceState?.value = Resource.Loading(it)
        statusState?.value = Resource.Loading(it)
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
    Resource.Error(e, retry)
} catch (e: Exception) {
    Resource.Error(UnknownResourceError(e), retry)
}
