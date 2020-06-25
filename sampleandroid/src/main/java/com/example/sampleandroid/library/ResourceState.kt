package com.example.sampleandroid.library

import androidx.compose.Composable
import androidx.compose.MutableState
import androidx.compose.mutableStateOf
import kim.jeonghyeon.androidlibrary.architecture.livedata.*
import kim.jeonghyeon.androidlibrary.architecture.livedata.loadResource
import kim.jeonghyeon.androidlibrary.architecture.net.error.UnknownResourceError
import kotlinx.coroutines.*

/**
 * if value is null. doesn't show
 */
typealias ResourceState<T> = MutableState<Resource<T>?>
typealias StatusState = MutableState<Resource<Any?>>

fun <T> resourceStateOf(): ResourceState<T> = mutableStateOf(null)

val <T> ResourceState<T>.successValue: T get() = value!!.get()!!

@Composable
fun <T> ResourceState<T>.success(block: @Composable() (T) -> Unit): ResourceState<T> {
    (value as? Resource.Success<T>?)?.let {
        block(it.data)
    }
    return this
}

val <T> ResourceState<T>.isSuccess: Boolean get() = value.isSuccessState()

@Suppress("UNCHECKED_CAST")
val <T> ResourceState<T>.successData: T
    get() = value!!.get() as T


/**
 * null -> invisible of success view
 * Success(null) -> visible if success view
 *
 *
 */
fun statusStateOf(): StatusState = mutableStateOf(Resource.Success(null))

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

fun <T> CoroutineScope.loadResource(
    liveData: LiveResource<T>? = null,
    state: LiveState? = null,
    work: suspend CoroutineScope.() -> T
) {
    //if error occurs in the async() before call await(), then crash occurs. this prevent the crash. but exeption occurs, so, exception will be catched in the getResource()
    launch(CoroutineExceptionHandler { _, _ -> }, CoroutineStart.LAZY) {
        getResource(work) {
            this@loadResource.loadResource(liveData, state, work)
        }?.also {
            liveData?.postValue(it)
            state?.postValue(it)
        }

    }.also {
        liveData?.setLoading(it)
        state?.setLoading(it)
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
} catch (e: ResourceException) {
    Resource.Error(e.error, retry)
} catch (e: Exception) {
    Resource.Error(UnknownResourceError(e), retry)
}