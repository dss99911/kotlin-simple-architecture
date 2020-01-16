package kim.jeonghyeon.androidlibrary.architecture.coroutine

import androidx.lifecycle.*
import kim.jeonghyeon.androidlibrary.architecture.livedata.LiveResource
import kim.jeonghyeon.androidlibrary.architecture.livedata.Resource
import kim.jeonghyeon.androidlibrary.architecture.livedata.ResourceState
import kim.jeonghyeon.androidlibrary.architecture.net.error.UnknownError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

fun <T> resourceLiveData(data: suspend () -> T): LiveResource<T> {
    return liveData {
        processLiveData(data)
    }
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

fun <T> CoroutineScope.loadResource(liveData: MutableLiveData<Resource<T>>? = null, work: suspend CoroutineScope.() -> T): Job {
    liveData?.postValue(Resource.Loading)
    return launch {
        val result = getResource(work) {
            loadResource(liveData, work)
        }
        liveData?.postValue(result)
    }
}

fun <T> CoroutineScope.loadResource(liveData: MutableLiveData<Resource<T>>? = null, work: suspend CoroutineScope.() -> T, onResult: Resource<T>.() -> Resource<T>): Job {
    liveData?.postValue(Resource.Loading)
    return launch {
        val result = getResource(work) {
            loadResource(liveData, work, onResult)
        }
        liveData?.postValue(onResult(result))
    }
}

fun <T> CoroutineScope.loadResource(liveData: MutableLiveData<Resource<T>>? = null, state: MutableLiveData<ResourceState>? = null, work: suspend CoroutineScope.() -> T): Job {
    liveData?.postValue(Resource.Loading)
    state?.postValue(Resource.Loading)
    return launch {
        val result = getResource(work) {
            loadResource(liveData, state, work)
        }
        liveData?.postValue(result)
        state?.postValue(result)
    }
}

fun <T> ViewModel.loadResource(liveData: MutableLiveData<Resource<T>>? = null, work: suspend CoroutineScope.() -> T): Job =
    viewModelScope.loadResource(liveData, work)

fun <T> ViewModel.loadResource(liveData: MutableLiveData<Resource<T>>? = null, work: suspend CoroutineScope.() -> T, onResult: Resource<T>.() -> Resource<T>): Job =
    viewModelScope.loadResource(liveData, work, onResult)

fun <T> ViewModel.loadResource(liveData: MutableLiveData<Resource<T>>? = null, state: MutableLiveData<ResourceState>? = null, work: suspend CoroutineScope.() -> T): Job =
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
