package kim.jeonghyeon.androidlibrary.architecture.livedata

import androidx.annotation.NonNull
import androidx.lifecycle.LiveData
import kim.jeonghyeon.androidlibrary.architecture.net.error.ResourceError
import kim.jeonghyeon.androidlibrary.architecture.net.error.UnknownError


typealias LiveResource<T> = BaseLiveData<Resource<T>>

fun <T> liveResource(value: Resource<T> = Resource.None): LiveResource<T> {
    return LiveResource(value)
}

fun <T> LiveResource<T>.postSuccess(data: T) {
    postValue(Resource.Success(data))
}

fun <T> LiveResource<T>.postLoading() {
    postValue(Resource.Loading)
}

fun <T> LiveResource<T>.postError(data: ResourceError) {
    postValue(Resource.Error(data))
}

fun <T> LiveResource<T>.getData(): T? = value?.get()

typealias ILiveState = IBaseLiveData<Resource<Any?>>
typealias LiveState = LiveResource<Any?>

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
            else -> (it as Resource<Y>).asLiveData()
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