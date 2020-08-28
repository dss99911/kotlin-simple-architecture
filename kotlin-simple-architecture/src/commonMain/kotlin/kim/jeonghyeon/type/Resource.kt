package kim.jeonghyeon.type

import kim.jeonghyeon.util.log

/**
 * Resource = data + status
 */
sealed class Resource<out T> {
    object Start : Resource<Nothing>()
    data class Loading<T>(val last: T? = null, val cancel: () -> Unit = { }) : Resource<T>()
    data class Success<T>(internal val dat: T) : Resource<T>()
    data class Error<T>(val error: ResourceError, val last: T? = null, val retry: () -> Unit = {}) : Resource<T>() {
        init {
            log.e(error)
        }
    }

    fun dataOrNull(): T? = when (this) {
        is Success -> dat
        is Loading -> last
        is Error -> last
        else -> null
    }

    fun data(): T {
        @Suppress("UNCHECKED_CAST")
        return dataOrNull() as T
    }

    fun successData(): T = if (isSuccess()) data() else error("Resource is not success")
    fun errorData(): ResourceError = if (this is Error) this.error else error("Resource is not error")
    fun <E : ResourceError> errorOf(): E = errorData() as E

    fun onLoading(onResult: (last: T?, cancel: () -> Unit) -> Unit): Resource<T> {
        if (this is Loading) {
            onResult(last, cancel)
        }

        return this
    }

    fun onSuccess(onResult: (T) -> Unit): Resource<T> {
        if (this is Success) {
            onResult(dat)
        }
        return this
    }

    inline fun onError(onResult: (error: ResourceError, last: T?, retry: () -> Unit) -> Unit): Resource<T> {
        if (this is Error) {
            onResult(error, last, retry)
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

    fun isStart() = this is Start
    fun isSuccess() = this is Success
    fun isLoading() = this is Loading
    fun isError() = this is Error
    fun isResult() = isSuccess() || isError()
    inline fun <reified E : ResourceError> isErrorOf() = this is Error && this.error is E

    fun asStatus(): Status {
        return this
    }

    companion object {
        //used for IOS
        fun createStart() {
            Resource.Start
        }
    }

    fun <U> map(change: (T) -> U): Resource<U> = when (this) {
        is Start -> Start
        is Loading -> Loading(last?.let(change), cancel)
        is Success -> Success(change(successData()))
        is Error -> Error(error, last?.let(change), retry)
    }
}

typealias Status = Resource<Any?>