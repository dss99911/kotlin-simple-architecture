package kim.jeonghyeon.type

import kim.jeonghyeon.util.log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Resource = data + status
 */
sealed class Resource<out T> {
    object Start : Resource<Nothing>()
    data class Loading<T>(val last: T? = null, val cancel: () -> Unit = { }) : Resource<T>()
    data class Success<T>(internal val dat: T) : Resource<T>()
    data class Error<T>(val error: ResourceError, val last: T? = null, val retry: () -> Unit = {}) : Resource<T>() {
        init {
            log(error)
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

typealias ResourceFlow<T> = Flow<Resource<T>>

/**
 * @param map this is invoked only on success, on other status, even if there is data. data will be converted to null
 */
fun <T, U> ResourceFlow<T>.successMap(map: (T) -> U): ResourceFlow<U> = map {
    when (it) {
        is Resource.Start -> it
        is Resource.Loading -> Resource.Loading(cancel = it.cancel)
        is Resource.Success -> it.map(map)
        is Resource.Error -> Resource.Error<U>(it.error, retry = it.retry)
    }
}

fun <T, U> ResourceFlow<T>.dataMap(map: (T) -> U): ResourceFlow<U> = map { it.map(map) }