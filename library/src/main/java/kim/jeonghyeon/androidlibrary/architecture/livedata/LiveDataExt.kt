package kim.jeonghyeon.androidlibrary.architecture.livedata

import androidx.annotation.NonNull
import androidx.lifecycle.*
import kim.jeonghyeon.androidlibrary.architecture.net.error.BaseError
import kim.jeonghyeon.androidlibrary.architecture.net.error.ExceptionError
import kim.jeonghyeon.androidlibrary.architecture.net.error.NeedRetryError
import java.util.concurrent.atomic.AtomicBoolean

typealias LiveString = LiveData<String>
typealias LiveBoolean = LiveData<Boolean>

fun <X, Y> LiveData<X>.map(@NonNull func: (X?) -> Y?): LiveData<Y> =
        Transformations.map(this, func)


fun <X> LiveData<X>.toMutable(): MutableLiveData<X> =
        if (this is MutableLiveData<X>) this
        else {
            val result = MediatorLiveData<X>()
            result.addSource(this) {
                result.value = it
            }
            result
        }

/**
 * set same value, so that, notify.
 */
fun <X> MutableLiveData<X>.repeat() {
    value = value
}

fun <X, Y> LiveData<Resource<X>>.mapOnSuccess(@NonNull func: (Resource<X>) -> Resource<Y>): LiveData<Resource<Y>> =
        Transformations.map(this) {
            when {
                it.isSuccess -> try {
                    func(it)
                } catch (e: Exception) {
                    val error = e as? BaseError ?: ExceptionError(e)
                    Resource.error<Y>(error)
                }
                it.data == null ->
                    @Suppress("UNCHECKED_CAST")
                    (it as Resource<Y>)
                else -> Resource<Y>(null, it.state)
            }
        }

fun <X, Y> LiveData<Resource<X>>.mapDataOnSuccess(@NonNull func: (X?) -> Y?): LiveData<Resource<Y>> =
        Transformations.map(this) {
            when {
                it.isSuccess -> try {
                    Resource(func(it.data), it.state)
                } catch (e: Exception) {
                    val error = e as? BaseError ?: ExceptionError(e)

                    Resource.error<Y>(error)
                }
                it.data == null ->
                    @Suppress("UNCHECKED_CAST")
                    (it as Resource<Y>)
                else -> Resource<Y>(null, it.state)
            }

        }

fun <X, Y> LiveData<X>.switchMap(@NonNull func: (X) -> LiveData<Y>): LiveData<Y> =
        Transformations.switchMap(this, func)

fun <X, Y> LiveData<Resource<X>>.switchMapOnSuccess(@NonNull func: (Resource<X>) -> LiveData<Resource<Y>>): LiveData<Resource<Y>> =
        Transformations.switchMap(this) {
            when {
                it.isSuccess -> try {
                    func(it)
                } catch (e: Exception) {
                    val error = e as? BaseError ?: ExceptionError(e)
                    Resource.error<Y>(error).asLiveData()
                }
                it.data == null ->
                    @Suppress("UNCHECKED_CAST")
                    (it as Resource<Y>).asLiveData()
                else -> Resource<Y>(null, it.state).asLiveData()
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

fun <T> mutableLiveDataOf(data: T?): MutableLiveData<T> = MutableLiveData<T>().apply { value = data }

fun <T> liveDataOf(data: () -> T?): LiveData<T> = object : LiveData<T>() {
    val isFirst = AtomicBoolean(true)
    override fun onActive() {
        super.onActive()
        if (isFirst.getAndSet(false)) {
            value = data()
        }
    }
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