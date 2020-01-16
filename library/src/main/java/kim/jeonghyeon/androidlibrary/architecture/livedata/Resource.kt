package kim.jeonghyeon.androidlibrary.architecture.livedata

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.observe
import kim.jeonghyeon.androidlibrary.architecture.net.error.ResourceError

sealed class Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>(), HasData
    data class Error(val error: ResourceError, val retry: () -> Unit = {}) : Resource<Nothing>()
    object Loading : Resource<Nothing>()

    object None : Resource<Nothing>()

    data class LoadingWithData<T>(val data: T) : Resource<T>(), HasData
    data class ErrorWithData<T>(val error: ResourceError, val data: T) : Resource<T>(), HasData

    interface HasData

    fun onSuccess(onResult: (T) -> Unit) {
        if (this is Success) {
            onResult(data)
        }
    }

    fun onNotSuccess(onResult: () -> Unit) {
        if (this !is Success) {
            onResult()
        }
    }

    fun onSuccessNull(onResult: () -> Unit) {
        if (this is Success && data == null) {
            onResult()
        }
    }

    fun onError(onResult: (ResourceError) -> Unit) {
        if (this is Error) {
            onResult(error)
        }
    }

    inline fun <reified E : ResourceError> onErrorOf(onResult: (E) -> Unit) {
        if (this is E) {
            onResult(this)
        }
    }

    fun onErrorWithData(onResult: (ResourceError, T?) -> Unit) {
        if (this is ErrorWithData) {
            onResult(error, data)
        }
    }

    fun onLoading(onResult: () -> Unit) {
        if (isLoading()) {
            onResult()
        }
    }

    fun onLoadingWithData(onResult: (T?) -> Unit) {
        if (this is LoadingWithData) {
            onResult(data)
        }
    }

    fun onNotLoading(onResult: () -> Unit) {
        if (!isLoading()) {
            onResult()
        }
    }

    fun data(): T? = when (this) {
        is Success -> data
        is ErrorWithData -> data
        is LoadingWithData -> data
        else -> null
    }

    fun getSuccessData(): T? = if (this is Success) {
        data
    } else {
        null
    }

    fun isSuccess() = this is Success
    fun isLoading() = this is Loading || this is LoadingWithData
    fun isError() = this is Error || this is ErrorWithData
}


fun Resource<*>?.isLoadingNotNull() = this?.isLoading() ?: false

fun Resource<*>?.isSuccessNotNull() = this?.isSuccess() ?: false


fun Resource<*>?.isErrorNotNull() = this?.isError() ?: false

fun <T> Resource<T>.asLiveData() = MutableLiveResource(this)


fun <T> LiveResource<T>.observeResource(
    owner: LifecycleOwner,
    onResult: Resource<T>.() -> Unit = {}
) {
    observe(owner) {
        onResult(it)
    }
}

typealias ResourceState = Resource<Any?>
val ResourceSuccess = Resource.Success(Unit)
val ResourceLoading = Resource.Loading