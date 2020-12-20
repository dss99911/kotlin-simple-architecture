package kim.jeonghyeon.type

import kim.jeonghyeon.util.log

/**
 * Resource = data + status
 */
sealed class Resource<out T> {
    /**
     * @param name this is used to show what is currently loading.
     */
    data class Loading(
        val name: String? = null,
        val retryData: () -> Unit = {}
    ) : Resource<Nothing>()

    data class Success<T>(val value: T, val retryData: () -> Unit = {}) : Resource<T>()
    data class Error(
        val errorData: ResourceError,
        val retryData: () -> Unit = {}
    ) : Resource<Nothing>()

    @Suppress("UNCHECKED_CAST")
    fun dataOrNull(): T? = if (this is Success) value
    else null

    @Suppress("UNCHECKED_CAST")
    fun data(): T = if (this is Success) value
    else error("data is empty")

    fun error(): ResourceError =
        if (this is Error) this.errorData else error("Resource is not error")

    fun errorOrNull(): ResourceError? = if (this is Error) this.errorData else null
    fun retryOnError() {
        if (this is Error) {
            this.retryData()
        }
    }

    fun retry() {
        when (this) {
            is Error -> retryData()
            is Loading -> retryData()
            is Success -> retryData()
        }
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified E : ResourceError> errorOrNullOf(): E? = errorOrNull() as? E?

    fun onLoading(onResult: (name: String?) -> Unit): Resource<T> {
        if (this is Loading) {
            onResult(this.name)
        }

        return this
    }

    inline fun onSuccess(onResult: (T) -> Unit): Resource<T> {
        if (this is Success) {
            onResult(value)
        }
        return this
    }

    inline fun onError(onResult: (error: ResourceError, last: T?, retry: () -> Unit) -> Unit): Resource<T> {
        if (this is Error) {
            onResult(errorData, dataOrNull(), retryData)
        }
        return this
    }

    inline fun <reified E : ResourceError> onErrorOf(onResult: (E) -> Unit): Resource<T> {
        onError { error, _, _ ->
            if (error is E) {
                onResult(error)
            }
        }

        return this
    }


    inline fun <reified E : ResourceError> isErrorOf() = this is Error && this.errorData is E

    fun asStatus(): Status {
        return this
    }

    private object Empty

    //used by ios
    fun isSuccess() = this is Success
    fun isLoading() = this is Loading
    fun isError() = this is Error
    fun isResult() = isSuccess() || isError()
}

fun Resource<*>?.isSuccess() = this != null && this is Resource.Success
fun Resource<*>?.isLoading() = this != null && this is Resource.Loading
fun Resource<*>?.isError() = this != null && this is Resource.Error
fun Resource<*>?.isResult() = isSuccess() || isError()

typealias Status = Resource<Any?>