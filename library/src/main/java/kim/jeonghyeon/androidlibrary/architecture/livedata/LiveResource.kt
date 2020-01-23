package kim.jeonghyeon.androidlibrary.architecture.livedata

import androidx.annotation.MainThread
import androidx.annotation.NonNull
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataScope
import androidx.lifecycle.liveData
import kim.jeonghyeon.androidlibrary.architecture.net.error.ResourceError
import kim.jeonghyeon.androidlibrary.architecture.net.error.UnknownError
import kotlinx.coroutines.*


typealias LiveResource<T> = LiveObject<Resource<T>>
typealias LiveState = LiveObject<State>

fun <T> liveResource(value: Resource<T> = Resource.None): LiveResource<T> {
    return LiveResource(value)
}

fun <T> LiveResource<T>.setSuccess(data: T) {
    value = Resource.Success(data)
}

fun <T> LiveResource<T>.postSuccess(data: T) {
    postValue(Resource.Success(data))
}

fun <T> LiveResource<T>.setLoading() {
    value = ResourceLoading
}

fun <T> LiveResource<T>.postLoading() {
    postValue(Resource.Loading)
}

fun <T> LiveResource<T>.postError(data: ResourceError) {
    postValue(Resource.Error(data))
}

fun <T> LiveResource<T>.getData(): T? = value?.get()

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
            //todo globalscope??
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

@MainThread
fun <T> CoroutineScope.loadResource(
    liveData: LiveResource<T>? = null,
    work: suspend CoroutineScope.() -> T
): Job {
    liveData?.setLoading()
    //if error occurs in the async() before call await(), then crash occurs. this prevent the crash. but exeption occurs, so, exception will be catched in the getResource()
    return launch(CoroutineExceptionHandler { _, _ -> }) {
        val result = getResource(work) {
            loadResource(liveData, work)
        }
        liveData?.postValue(result)
    }
}


@MainThread
fun <T, U> CoroutineScope.loadResourcePartialRetryable(
    liveData: LiveResource<U>? = null,
    part1: suspend CoroutineScope.() -> T,
    part2: suspend CoroutineScope.(T) -> U
): Job {
    liveData?.setLoading()
    //if error occurs in the async() before call await(), then crash occurs. this prevent the crash. but exeption occurs, so, exception will be catched in the getResource()
    return launch(CoroutineExceptionHandler { _, _ -> }) {

        val result1 = getResource(part1) {
            loadResourcePartialRetryable(liveData, part1, part2)
        }
        val result2 = getNextResource(result1, part2) {
            loadResource(liveData) {
                part2((result1 as Resource.Success).data)
            }
        }

        liveData?.postValue(result2)
    }
}

@MainThread
fun <T, U, V> CoroutineScope.loadResourcePartialRetryable(
    liveData: LiveResource<V>? = null,
    part1: suspend CoroutineScope.() -> T,
    part2: suspend CoroutineScope.(T) -> U,
    part3: suspend CoroutineScope.(U) -> V
): Job {
    liveData?.setLoading()

    //if error occurs in the async() before call await(), then crash occurs. this prevent the crash. but exeption occurs, so, exception will be catched in the getResource()
    return launch(CoroutineExceptionHandler { _, _ -> }) {

        val result1 = getResource(part1) {
            loadResourcePartialRetryable(liveData, part1, part2, part3)
        }

        val result2 = getNextResource(result1, part2) {
            loadResourcePartialRetryable(liveData, {
                part2((result1 as Resource.Success).data)
            }, part3)
        }

        val result3 = getNextResource(result2, part3) {
            loadResource(liveData) {
                part3((result2 as Resource.Success).data)
            }
        }

        liveData?.postValue(result3)
    }

}

@MainThread
fun <T, U, V, W> CoroutineScope.loadResourcePartialRetryable(
    liveData: LiveResource<W>? = null,
    part1: suspend CoroutineScope.() -> T,
    part2: suspend CoroutineScope.(T) -> U,
    part3: suspend CoroutineScope.(U) -> V,
    part4: suspend CoroutineScope.(V) -> W
): Job {
    liveData?.setLoading()

//if error occurs in the async() before call await(), then crash occurs. this prevent the crash. but exeption occurs, so, exception will be catched in the getResource()
    return launch(CoroutineExceptionHandler { _, _ -> }) {

        val result1 = getResource(part1) {
            loadResourcePartialRetryable(liveData, part1, part2, part3, part4)
        }

        val result2 = getNextResource(result1, part2) {
            loadResourcePartialRetryable(liveData, {
                part2((result1 as Resource.Success).data)
            }, part3, part4)
        }

        val result3 = getNextResource(result2, part3) {
            loadResourcePartialRetryable(liveData, {
                part3((result2 as Resource.Success).data)
            }, part4)
        }

        val result4 = getNextResource(result3, part4) {
            loadResource(liveData) {
                part4((result3 as Resource.Success).data)
            }
        }

        liveData?.postValue(result4)
    }

}

private suspend fun <T, U> CoroutineScope.getNextResource(
    previous: Resource<T>,
    part2: suspend CoroutineScope.(T) -> U,
    retry: () -> Unit
): Resource<U> {
    val result2 = if (previous is Resource.Success) {
        getResource({ part2(previous.data) }, retry)
    } else {
        previous as Resource<Nothing>
    }
    return result2
}


@MainThread
fun <T> CoroutineScope.loadResource(
    liveData: LiveResource<T>? = null,
    work: suspend CoroutineScope.() -> T,
    onResult: (Resource<T>) -> Resource<T>
): Job {
    liveData?.setLoading()
    //if error occurs in the async() before call await(), then crash occurs. this prevent the crash. but exeption occurs, so, exception will be catched in the getResource()
    return launch(CoroutineExceptionHandler { _, _ -> }) {
        val result = getResource(work) {
            loadResource(liveData, work, onResult)
        }
        liveData?.postValue(onResult(result))
    }
}

@MainThread
fun <T> CoroutineScope.loadResource(
    liveData: LiveResource<T>? = null,
    state: LiveState? = null,
    work: suspend CoroutineScope.() -> T
): Job {
    liveData?.setLoading()
    state?.setLoading()
    //if error occurs in the async() before call await(), then crash occurs. this prevent the crash. but exeption occurs, so, exception will be catched in the getResource()
    return launch(CoroutineExceptionHandler { _, _ -> }) {
        val result = getResource(work) {
            loadResource(liveData, state, work)
        }
        liveData?.postValue(result)
        state?.postValue(result)
    }
}

private suspend fun <T> CoroutineScope.getResource(
    action: suspend CoroutineScope.() -> T,
    retry: () -> Unit
): Resource<T> = try {
    Resource.Success(action(this))
} catch (e: ResourceException) {
    Resource.Error(e.error, retry)
} catch (e: Exception) {
    Resource.Error(UnknownError(e), retry)
}


fun <X, Y> LiveResource<X>.successDataMap(@NonNull func: (X) -> Y): LiveResource<Y> = map {
    @Suppress("UNCHECKED_CAST")
    when (it) {
        is Resource.Success -> try {
            Resource.Success(func(it.data))
        } catch (e: Exception) {
            val error = e as? ResourceError ?: UnknownError(e)
            error.asResource()
        }
        else -> it as Resource<Y>
    }
}

fun <X, Y> LiveResource<X>.successSwitchMap(@NonNull func: (X) -> LiveResource<Y>): LiveResource<Y> =
    switchMap {
        @Suppress("UNCHECKED_CAST")
        when (it) {
            is Resource.Success -> try {
                func(it.data)
            } catch (e: Exception) {
                val error = e as? ResourceError ?: UnknownError(e)
                error.asLiveResource<Y>()
            }
            else -> (it as Resource<Y>).asLive()
        }
    }


fun <X, Y> LiveResource<X>.successMap(@NonNull func: (X) -> Resource<Y>): LiveResource<Y> = map {
    @Suppress("UNCHECKED_CAST")
    when (it) {
        is Resource.Success -> try {
            func(it.data)
        } catch (e: Exception) {
            val error = e as? ResourceError ?: UnknownError(e)
            error.asResource()
        }
        else -> it as Resource<Y>
    }
}

fun <T> LiveData<Resource<T>>.asResource(): LiveResource<T> = liveResource<T>().apply {
    plusAssign(this@asResource)
}

/**
 * don't use this after other async(), if the async() error occrus, this should be stopped but because of try catch, this is not stopped.
 * @param action returns data and not polling again. but if exception occurs, polling
 */
@Throws(PollingException::class)
suspend inline fun <T> polling(
    count: Int,
    initialDelay: Long,
    delayMillis: Long,
    action: (index: Int) -> T
): T {
    delay(initialDelay)
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
