package kim.jeonghyeon.androidlibrary.architecture.livedata

import androidx.annotation.MainThread
import androidx.annotation.NonNull
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataScope
import androidx.lifecycle.liveData
import kim.jeonghyeon.androidlibrary.architecture.net.error.ResourceError
import kim.jeonghyeon.androidlibrary.architecture.net.error.UnknownError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


typealias LiveResource<T> = BaseLiveData<Resource<T>>
typealias LiveState = BaseLiveData<State>

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
    return launch {
        val result = getResource(work) {
            loadResource(liveData, work)
        }
        liveData?.postValue(result)
    }
}

@MainThread
fun <T> CoroutineScope.loadResource(
    liveData: LiveResource<T>? = null,
    work: suspend CoroutineScope.() -> T,
    onResult: (Resource<T>) -> Resource<T>
): Job {
    liveData?.setLoading()
    return launch {
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
    return launch {
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