package kim.jeonghyeon.androidlibrary.architecture.livedata

import androidx.annotation.MainThread
import androidx.annotation.NonNull
import androidx.lifecycle.LiveData
import kim.jeonghyeon.androidlibrary.architecture.net.error.ResourceError
import kim.jeonghyeon.androidlibrary.architecture.net.error.UnknownError
import kotlinx.coroutines.*

typealias LiveResource<T> = LiveObject<Resource<T>>
typealias LiveState = LiveObject<State>

//region functions

fun <T> liveResource(value: Resource<T> = Resource.None): LiveResource<T> {
    return LiveResource(value)
}

fun <T> LiveResource<T>.setSuccess(data: T) {
    value = Resource.Success(data)
}

fun <T> LiveResource<T>.postSuccess(data: T) {
    postValue(Resource.Success(data))
}

fun <T> LiveResource<T>.setLoading(job: Job) {
    value = Resource.Loading(job)
}

fun <T> LiveResource<T>.postError(data: ResourceError) {
    postValue(Resource.Error(data))
}

fun <T> LiveResource<T>.getData(): T? = value?.get()

fun <T> LiveData<Resource<T>>.asResource(): LiveResource<T> = liveResource<T>().apply {
    plusAssign(this@asResource)
}


//endregion functions

//region loadResource

@MainThread
fun <T> CoroutineScope.loadResource(
    liveData: LiveResource<T>? = null,
    work: suspend CoroutineScope.() -> T
) {
    //if error occurs in the async() before call await(), then crash occurs. this prevent the crash. but exeption occurs, so, exception will be catched in the getResource()
    launch(CoroutineExceptionHandler { _, _ -> }, CoroutineStart.LAZY) {
        getResource(work) {
            loadResource(liveData, work)
        }?.also { liveData?.postValue(it) }

    }.also {
        liveData?.setLoading(it)
        it.start()
    }
}

@MainThread
fun <T> CoroutineScope.loadResource(
    liveData: LiveResource<T>? = null,
    work: suspend CoroutineScope.() -> T,
    onResult: (Resource<T>) -> Resource<T>
) {
    //if error occurs in the async() before call await(), then crash occurs. this prevent the crash. but exeption occurs, so, exception will be catched in the getResource()
    launch(CoroutineExceptionHandler { _, _ -> }, CoroutineStart.LAZY) {
        getResource(work) {
            loadResource(liveData, work, onResult)
        }?.also { liveData?.postValue(onResult(it)) }

    }.also {
        liveData?.setLoading(it)
        it.start()
    }
}

@MainThread
fun <T> CoroutineScope.loadResource(
    liveData: LiveResource<T>? = null,
    state: LiveState? = null,
    work: suspend CoroutineScope.() -> T
) {
    //if error occurs in the async() before call await(), then crash occurs. this prevent the crash. but exeption occurs, so, exception will be catched in the getResource()
    launch(CoroutineExceptionHandler { _, _ -> }, CoroutineStart.LAZY) {
        getResource(work) {
            loadResource(liveData, state, work)
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

/**
 * @return if it's cancelled, return null for ignoring
 */
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
    Resource.Error(UnknownError(e), retry)
}

//endregion loadResource

//region success map

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

//endregion success map

//region polling

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

//endregion polling

//region partial retry

@MainThread
fun <T, U> CoroutineScope.loadResourcePartialRetryable(
    liveData: LiveResource<U>? = null,
    part1: suspend CoroutineScope.() -> T,
    part2: suspend CoroutineScope.(T) -> U
) {
    //if error occurs in the async() before call await(), then crash occurs. this prevent the crash. but exeption occurs, so, exception will be catched in the getResource()
    launch(CoroutineExceptionHandler { _, _ -> }) {

        val result1 = getResource(part1) {
            loadResourcePartialRetryable(liveData, part1, part2)
        } ?: return@launch//if it's cancelled, ignore next
        val result2 = getNextResource(result1, part2) {
            loadResource(liveData) {
                part2((result1 as Resource.Success).data)
            }
        } ?: return@launch

        liveData?.postValue(result2)
    }.also {
        liveData?.setLoading(it)
    }
}

@MainThread
fun <T, U, V> CoroutineScope.loadResourcePartialRetryable(
    liveData: LiveResource<V>? = null,
    part1: suspend CoroutineScope.() -> T,
    part2: suspend CoroutineScope.(T) -> U,
    part3: suspend CoroutineScope.(U) -> V
) {
    //if error occurs in the async() before call await(), then crash occurs. this prevent the crash. but exeption occurs, so, exception will be catched in the getResource()
    launch(CoroutineExceptionHandler { _, _ -> }) {

        val result1 = getResource(part1) {
            loadResourcePartialRetryable(liveData, part1, part2, part3)
        } ?: return@launch

        val result2 = getNextResource(result1, part2) {
            loadResourcePartialRetryable(liveData, {
                part2((result1 as Resource.Success).data)
            }, part3)
        } ?: return@launch

        val result3 = getNextResource(result2, part3) {
            loadResource(liveData) {
                part3((result2 as Resource.Success).data)
            }
        } ?: return@launch

        liveData?.postValue(result3)
    }.also {
        liveData?.setLoading(it)
    }

}

@MainThread
fun <T, U, V, W> CoroutineScope.loadResourcePartialRetryable(
    liveData: LiveResource<W>? = null,
    part1: suspend CoroutineScope.() -> T,
    part2: suspend CoroutineScope.(T) -> U,
    part3: suspend CoroutineScope.(U) -> V,
    part4: suspend CoroutineScope.(V) -> W
) {

//if error occurs in the async() before call await(), then crash occurs. this prevent the crash. but exeption occurs, so, exception will be catched in the getResource()
    launch(CoroutineExceptionHandler { _, _ -> }) {

        val result1 = getResource(part1) {
            loadResourcePartialRetryable(liveData, part1, part2, part3, part4)
        } ?: return@launch

        val result2 = getNextResource(result1, part2) {
            loadResourcePartialRetryable(liveData, {
                part2((result1 as Resource.Success).data)
            }, part3, part4)
        } ?: return@launch

        val result3 = getNextResource(result2, part3) {
            loadResourcePartialRetryable(liveData, {
                part3((result2 as Resource.Success).data)
            }, part4)
        } ?: return@launch

        val result4 = getNextResource(result3, part4) {
            loadResource(liveData) {
                part4((result3 as Resource.Success).data)
            }
        } ?: return@launch

        liveData?.postValue(result4)
    }.also {
        liveData?.setLoading(it)
    }

}

private suspend fun <T, U> CoroutineScope.getNextResource(
    previous: Resource<T>,
    part2: suspend CoroutineScope.(T) -> U,
    retry: () -> Unit
): Resource<U>? {
    val result2 = if (previous is Resource.Success) {
        getResource({ part2(previous.data) }, retry)
    } else {
        @Suppress("UNCHECKED_CAST")
        previous as Resource<Nothing>
    }
    return result2
}
//endregion partial retry
