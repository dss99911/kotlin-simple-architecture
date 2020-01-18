package kim.jeonghyeon.androidlibrary.architecture.coroutine

import androidx.lifecycle.LiveDataScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kim.jeonghyeon.androidlibrary.architecture.livedata.*
import kim.jeonghyeon.androidlibrary.architecture.net.error.UnknownError
import kotlinx.coroutines.*

fun <T> liveResource(data: suspend () -> T): LiveResource<T> = liveResource<T>().apply {
    plusAssign(liveData {
        processLiveData(data)
    })
}

private suspend fun <T> LiveDataScope<Resource<T>>.processLiveData(
    data: suspend () -> T
) {
    emit(Resource.Loading)

    try {
        Resource.Success(data())
    } catch (e: ResourceException) {
        Resource.Error(e.error) {
            GlobalScope.launch {
                processLiveData(data)
            }
        }
    } catch (e: Exception) {
        Resource.Error(UnknownError(e))
    }.let {
        emit(it)
    }
}

fun <T> CoroutineScope.loadResource(
    liveData: LiveResource<T>? = null,
    work: suspend CoroutineScope.() -> T
): Job {
    liveData?.postLoading()
    return launch {
        val result = getResource(work) {
            loadResource(liveData, work)
        }
        liveData?.postValue(result)
    }
}

fun <T> CoroutineScope.loadResource(
    liveData: LiveResource<T>? = null,
    work: suspend CoroutineScope.() -> T,
    onResult: (Resource<T>) -> Resource<T>
): Job {
    liveData?.postLoading()
    return launch {
        val result = getResource(work) {
            loadResource(liveData, work, onResult)
        }
        liveData?.postValue(onResult(result))
    }
}

fun <T> CoroutineScope.loadResource(
    liveData: LiveResource<T>? = null,
    state: LiveState? = null,
    work: suspend CoroutineScope.() -> T
): Job {
    liveData?.postLoading()
    state?.postLoading()
    return launch {
        val result = getResource(work) {
            loadResource(liveData, state, work)
        }
        liveData?.postValue(result)
        state?.postValue(result)
    }
}

fun <T> ViewModel.loadResource(
    liveData: LiveResource<T>? = null,
    work: suspend CoroutineScope.() -> T
): Job =
    viewModelScope.loadResource(liveData, work)

fun <T> ViewModel.loadResource(
    liveData: LiveResource<T>? = null,
    work: suspend CoroutineScope.() -> T,
    onResult: (Resource<T>) -> Resource<T>
): Job =
    viewModelScope.loadResource(liveData, work, onResult)

fun <T> ViewModel.loadResource(
    liveData: LiveResource<T>? = null,
    state: LiveState? = null,
    work: suspend CoroutineScope.() -> T
): Job =
    viewModelScope.loadResource(liveData, state, work)

inline fun <T> ViewModel.launch(crossinline work: suspend CoroutineScope.() -> T): Job {
    return viewModelScope.launch {
        work()
    }
}

suspend fun <T> CoroutineScope.getResource(
    action: suspend CoroutineScope.() -> T,
    retry: () -> Unit
): Resource<T> = try {
    Resource.Success(action(this))
} catch (e: ResourceException) {
    Resource.Error(e.error, retry)
} catch (e: Exception) {
    Resource.Error(UnknownError(e), retry)
}

/**
 * @param action returns data and not polling again. but if exception occurs, polling
 */
suspend inline fun <T> polling(
    count: Int,
    delayMillis: Long,
    action: (index: Int) -> T
): T {
    repeat(count) { repeatIndex ->
        try {
            return action(repeatIndex)
        } catch (e: Exception) {
            //retry
            delay(delayMillis)
        }
    }
    throw PollingException()
}

class PollingException : RuntimeException()