package kim.jeonghyeon.androidlibrary.architecture.livedata

import kim.jeonghyeon.androidlibrary.architecture.net.error.ResourceError

sealed class Resource<out T> {
    object None : Resource<Nothing>()
    object Loading : Resource<Nothing>()
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val error: ResourceError, val retry: () -> Unit = {}) : Resource<Nothing>()

    fun get(): T? = when (this) {
        is Success -> data
        else -> null
    }

    fun onSuccess(onResult: (T) -> Unit): Resource<T> {
        if (this is Success) {
            onResult(data)
        }
        return this
    }

    fun onError(onResult: (ResourceError) -> Unit): Resource<T> {
        if (this is Error) {
            onResult(error)
        }
        return this
    }

    inline fun <reified E : ResourceError> onErrorOf(onResult: (E) -> Unit): Resource<T> {
        if (this is E) {
            onResult(this)
        }
        return this
    }

    fun isSuccess() = this is Success
    fun isLoading() = this is Loading
    fun isError() = this is Error
    inline fun <reified E : ResourceError> isErrorOf() = this is Error && this.error is E
}


fun Resource<*>?.isLoadingNotNull() = this?.isLoading() ?: false

fun Resource<*>?.isSuccessNotNull() = this?.isSuccess() ?: false


fun Resource<*>?.isErrorNotNull() = this?.isError() ?: false

fun <T> Resource<T>.asLiveData() = liveResource(this)


typealias State = Resource<Any?>

val ResourceSuccess = Resource.Success(Unit)
val ResourceLoading = Resource.Loading

class ResourceException(val error: ResourceError) : Exception()