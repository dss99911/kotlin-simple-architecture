package kim.jeonghyeon.type

import kim.jeonghyeon.util.log
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Resource = data + status
 */
sealed class Resource<out T> {
    object Start : Resource<Nothing>()
    data class Loading(val job: Job? = null) : Resource<Nothing>()
    data class Success<T>(internal val dat: T) : Resource<T>()
    data class Error(val error: ResourceError, val retry: () -> Unit = {}) : Resource<Nothing>() {
        init {
            log(error)
        }
    }

    fun dataOrNull(): T? = when (this) {
        is Success -> dat
        else -> null
    }

    fun data(): T = (this as Success).dat

    fun onLoading(onResult: (job: Job?) -> Unit): Resource<T> {
        if (this is Loading) {
            onResult(job)
        }

        return this
    }

    fun onSuccess(onResult: (T) -> Unit): Resource<T> {
        if (this is Success) {
            onResult(dat)
        }
        return this
    }

    fun onError(onResult: (Error) -> Unit): Resource<T> {
        if (this is Error) {
            onResult(this)
        }
        return this
    }

    inline fun <reified E : ResourceError> onErrorOf(onResult: (E) -> Unit): Resource<T> {
        if (this is E) {
            onResult(this)
        }
        return this
    }

    fun isStart() = this is Start
    fun isSuccess() = this is Success
    fun isLoading() = this is Loading
    fun isError() = this is Error
    fun isResult() = isSuccess() || isError()
    inline fun <reified E : ResourceError> isErrorOf() = this is Error && this.error is E
}

typealias Status = Resource<Any?>

typealias ResourceFlow<T> = Flow<Resource<T>>

fun <T, U> ResourceFlow<T>.successMap(map: (T) -> U): ResourceFlow<U> = map {
    if (it.isSuccess()) {
        Resource.Success(map(it.data()))
    } else {
        it as Resource<Nothing>
    }
}