package kim.jeonghyeon.androidlibrary.deprecated

import android.os.Handler
import androidx.annotation.NonNull
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import kim.jeonghyeon.androidlibrary.architecture.livedata.MediatorResourceLiveData
import kim.jeonghyeon.androidlibrary.architecture.livedata.Resource
import kim.jeonghyeon.androidlibrary.architecture.livedata.ResourceLiveData
import kim.jeonghyeon.androidlibrary.architecture.livedata.successDataMap

fun <A, B, OUT> LiveData<A>.merge(source: LiveData<B>, observer: (A, B) -> OUT): LiveData<OUT> {
    val result = MediatorLiveData<OUT>()
    result.addSource(this) {
        result.value = observer(this.value as A, source.value as B)
    }
    result.addSource(source) {
        result.value = observer(this.value as A, source.value as B)
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

fun <X> ResourceLiveData<X>.successApply(@NonNull func: (X) -> Unit): ResourceLiveData<X> {
    return successDataMap {
        func(it)
        it
    }
}

//fun <X, Y> ResourceLiveData<X>.switchResourceMapOnSuccess(@NonNull func: (Resource.Success<X>) -> ResourceLiveData<Y>): ResourceLiveData<Y> =
//    switchMap {
//        when (it) {
//            is Resource.Success -> try {
//                func(it)
//            } catch (e: Exception) {
//                val error = e as? ResourceError ?: UnknownError(e)
//                error.asResourceLiveData<Y>()
//            }
//            is Resource.HasData -> (it.asEmptyData() as Resource<Y>).asLiveData()
//            else -> (it as Resource<Y>).asLiveData()
//        }
//    }

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