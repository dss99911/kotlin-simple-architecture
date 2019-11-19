package kim.jeonghyeon.androidlibrary.architecture.livedata

import android.os.Handler
import androidx.annotation.NonNull
import androidx.lifecycle.*
import kim.jeonghyeon.androidlibrary.architecture.net.error.ResourceError
import kim.jeonghyeon.androidlibrary.architecture.net.error.UnknownError

typealias LiveString = LiveData<String>
typealias LiveBoolean = LiveData<Boolean>
typealias ResourceLiveData<T> = LiveData<Resource<T>>
typealias MutableResourceLiveData<T> = MutableLiveData<Resource<T>>
typealias MediatorResourceLiveData<T> = MediatorLiveData<Resource<T>>

fun <X, Y> LiveData<X>.map(@NonNull func: (X?) -> Y?): LiveData<Y> =
        Transformations.map(this, func)


fun <X> LiveData<X>.toMutable(): MutableLiveData<X> =
        if (this is MutableLiveData<X>) this
        else {
            MediatorLiveData<X>().apply {
               this += this@toMutable
            }
        }

operator fun <T> MediatorLiveData<T>.plusAssign(other: LiveData<T>) {
    addSource(other, ::setValue)
}

fun <T> MediatorLiveData<T>.receive(other: () -> LiveData<T>) {
    addSource(other(), ::setValue)
}


fun <T> MutableResourceLiveData<T>.postSuccess(data: T) {
    postValue(Resource.Success(data))
}

fun <T> MutableResourceLiveData<T>.postLoading() {
    postValue(Resource.Loading)
}

fun <T> MutableResourceLiveData<T>.postError(data: ResourceError) {
    postValue(Resource.Error(data))
}


/**
 * set same value, so that, notify.
 */
fun <X> MutableLiveData<X>.repeat() {
    value = value
}

fun <X, Y> LiveData<X>.switchMap(@NonNull func: (X) -> LiveData<Y>?): LiveData<Y> =
    this.switchMap(func)

fun <X, Y> ResourceLiveData<X>.switchMapOnSuccess(@NonNull func: (X) -> ResourceLiveData<Y>): ResourceLiveData<Y> =
    this.switchMap {
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
fun <T> LiveData<T>.observeOneTime(@NonNull func: (T?) -> Boolean) {
    observeForever(object : Observer<T> {
        override fun onChanged(t: T?) {
            if (func(t)) {
                removeObserver(this)
            }
        }
    })
}

fun <A, B, OUT> LiveData<A>.merge(source: LiveData<B>, observer: (A?, B?) -> OUT?): LiveData<OUT> {
    val result = MediatorLiveData<OUT>()
    result.addSource(this) {
        result.value = observer(this.value, source.value)
    }
    result.addSource(source) {
        result.value = observer(this.value, source.value)
    }

    return result
}

fun <A, B, OUT> LiveData<A>.mergeNotNull(source: LiveData<B>, observer: (A, B) -> OUT?): LiveData<OUT> {
    val result = MediatorLiveData<OUT>()
    result.addSource(this) {
        result.value = observer(this.value ?: return@addSource, source.value ?: return@addSource)
    }
    result.addSource(source) {
        result.value = observer(this.value ?: return@addSource, source.value ?: return@addSource)
    }

    return result
}

fun <T> ResourceLiveData<T>.getData(): T? {
    return value?.data()
}

fun <X, Y> ResourceLiveData<X>.mapOnSuccess(@NonNull func: (X) -> Resource<Y>): ResourceLiveData<Y> =
    map {
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

fun <X> ResourceLiveData<X>.applyDataOnSuccess(@NonNull func: (X) -> Unit): ResourceLiveData<X> {
    return mapDataOnSuccess {
        func(it)
        it
    }
}

fun <X, Y> ResourceLiveData<X>.mapDataOnSuccess(@NonNull func: (X) -> Y): ResourceLiveData<Y> =
    map {
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


fun <X, Y> ResourceLiveData<X>.switchResourceMapOnSuccess(@NonNull func: (Resource.Success<X>) -> ResourceLiveData<Y>): ResourceLiveData<Y> =
    this.switchMap {
        when (it) {
            is Resource.Success -> try {
                func(it)
            } catch (e: Exception) {
                val error = e as? ResourceError ?: UnknownError(e)
                error.asResourceLiveData<Y>()
            }
            is Resource.HasData -> (it.asEmptyData() as Resource<Y>).asLiveData()
            else -> (it as Resource<Y>).asLiveData()
        }
    }

/**
 * @param func : return delay time
 */
fun <X> ResourceLiveData<X>.delay(@NonNull func: (Resource<X>) -> Long): ResourceLiveData<X> {
    val mediatorLiveData = MediatorResourceLiveData<X>()
    mediatorLiveData.addSource(this) {
        if (it == null) {
            mediatorLiveData.postValue(it)
            return@addSource
        }
        val duration = func(it)

        if (duration == 0L) {
            mediatorLiveData.postValue(it)
        } else {
            //todo if coroutine is applied, consider kotlin coroutine delay
            Handler().postDelayed({
                mediatorLiveData.postValue(it)
            }, func(it))
        }
    }

    return mediatorLiveData
}



private fun Resource.HasData.asEmptyData():Resource<Nothing> {
    return when (this) {
        is Resource.ErrorWithData<*> -> Resource.Error(error)
        is Resource.LoadingWithData<*> -> Resource.Loading
        else -> throw IllegalStateException()
    }
}

