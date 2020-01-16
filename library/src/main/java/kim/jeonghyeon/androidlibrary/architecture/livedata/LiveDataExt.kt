package kim.jeonghyeon.androidlibrary.architecture.livedata

import androidx.annotation.NonNull
import androidx.lifecycle.*
import kim.jeonghyeon.androidlibrary.architecture.net.error.ResourceError
import kim.jeonghyeon.androidlibrary.architecture.net.error.UnknownError

typealias LiveResource<T> = LiveData<Resource<T>>
typealias MutableLiveResource<T> = MutableLiveData<Resource<T>>
typealias MediatorLiveResource<T> = MediatorLiveData<Resource<T>>

fun <T> liveResource() = MediatorLiveResource<T>().apply {
    value = Resource.None
}

fun liveState() = MediatorLiveData<ResourceState>().apply {
    value = Resource.None
}

fun <X, Y> LiveData<X>.map(@NonNull func: (X) -> Y): LiveData<Y> =
        Transformations.map(this, func)


fun <X> LiveData<X>.toMutable(): MutableLiveData<X> =
        if (this is MutableLiveData<X>) this
        else {
            MediatorLiveData<X>().apply {
               this += this@toMutable
            }
        }

operator fun <T> MediatorLiveData<T>.plusAssign(other: LiveData<out T>) {
    addSource(other, ::setValue)
}

fun <T> MediatorLiveData<T>.receive(other: () -> LiveData<T>) {
    addSource(other(), ::setValue)
}


fun <T> MutableLiveResource<T>.postSuccess(data: T) {
    postValue(Resource.Success(data))
}

fun <T> MutableLiveResource<T>.postLoading() {
    postValue(Resource.Loading)
}

fun <T> MutableLiveResource<T>.postError(data: ResourceError) {
    postValue(Resource.Error(data))
}


/**
 * set same value, so that, notify.
 */
fun <X> MutableLiveData<X>.repeat() {
    value = value
}

fun <X, Y> LiveResource<X>.successSwitchMap(@NonNull func: (X) -> LiveResource<Y>): LiveResource<Y> =
    switchMap {
        when (it) {
            is Resource.Success -> try {
                func(it.data)
            } catch (e: Exception) {
                val error = e as? ResourceError ?: UnknownError(e)
                error.asResourceLiveData<Y>()
            }
            is Resource.HasData -> (it.asEmptyData() as Resource<Y>).asLiveData()
            else -> (it as Resource<Y>).asLiveData()
        }
    }

/**
 * if return is true, remove observer
 */
fun <T> LiveData<T>.observeOneTime(@NonNull func: (T) -> Boolean) {
    observeForever(object : Observer<T> {
        override fun onChanged(t: T) {
            if (func(t)) {
                removeObserver(this)
            }
        }
    })
}


fun <T> LiveResource<T>.getData(): T? {
    return value?.data()
}

fun <X, Y> LiveResource<X>.successMap(@NonNull func: (X) -> Resource<Y>): LiveResource<Y> = map {
    when (it) {
        is Resource.Success -> try {
            func(it.data)
        } catch (e: Exception) {
            val error = e as? ResourceError ?: UnknownError(e)
            error.asResource()
        }
        is Resource.HasData -> it.asEmptyData()
        else -> it as Resource<Y>
    }
}

fun <X, Y> LiveResource<X>.successDataMap(@NonNull func: (X) -> Y): LiveResource<Y> = map {
    when (it) {
        is Resource.Success -> try {
            Resource.Success(func(it.data))
        } catch (e: Exception) {
            val error = e as? ResourceError ?: UnknownError(e)
            error.asResource()
        }
        is Resource.HasData -> it.asEmptyData()
        else -> it as Resource<Y>
    }
}


private fun Resource.HasData.asEmptyData():Resource<Nothing> {
    return when (this) {
        is Resource.ErrorWithData<*> -> Resource.Error(error)
        is Resource.LoadingWithData<*> -> Resource.Loading
        else -> throw IllegalStateException()
    }
}

