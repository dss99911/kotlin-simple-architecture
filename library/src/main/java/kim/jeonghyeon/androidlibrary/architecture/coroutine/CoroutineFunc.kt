package kim.jeonghyeon.androidlibrary.architecture.coroutine

import androidx.lifecycle.*
import kim.jeonghyeon.androidlibrary.architecture.livedata.Resource
import kim.jeonghyeon.androidlibrary.architecture.livedata.ResourceLiveData
import kim.jeonghyeon.androidlibrary.architecture.net.error.UnknownError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

fun <T> resourceLiveData(data: suspend LiveDataScope<Resource<T>>.() -> T): ResourceLiveData<T> {
    return liveData(Dispatchers.IO) {
        emit(Resource.Loading)

        try {
            Resource.Success(data(this))
        } catch (e: ResourceException) {
            Resource.Error(e.error)
        } catch (e: Exception) {
            Resource.Error(UnknownError(e))
        }.let {
            emit(it)
        }
    }
}

fun <T> CoroutineScope.loadResource(liveData: MutableLiveData<Resource<T>>? = null, work: suspend CoroutineScope.() -> T): Job {
    liveData?.postValue(Resource.Loading)
    return launch {
        val result = getResource(work)
        liveData?.postValue(result)
    }
}

fun <T> CoroutineScope.loadResource(liveData: MutableLiveData<Resource<T>>? = null, work: suspend CoroutineScope.() -> T, onResult: Resource<T>.() -> Resource<T>): Job {
    liveData?.postValue(Resource.Loading)
    return launch {
        val result = getResource(work)
        liveData?.postValue(onResult(result))
    }
}

fun <T> ViewModel.loadResource(liveData: MutableLiveData<Resource<T>>? = null, work: suspend CoroutineScope.() -> T): Job =
    viewModelScope.loadResource(liveData, work)

fun <T> ViewModel.loadResource(liveData: MutableLiveData<Resource<T>>? = null, work: suspend CoroutineScope.() -> T, onResult: Resource<T>.() -> Resource<T>): Job =
    viewModelScope.loadResource(liveData, work, onResult)

inline fun <T> ViewModel.launch(crossinline work: suspend CoroutineScope.() -> T): Job {
    return viewModelScope.launch {
        work()
    }
}


suspend fun <T> CoroutineScope.getResource(action: suspend CoroutineScope.() -> T): Resource<T> = try {
    Resource.Success(action(this))
} catch (e: ResourceException) {
    Resource.Error(e.error)
} catch (e: Exception) {
    Resource.Error(UnknownError(e))
}
