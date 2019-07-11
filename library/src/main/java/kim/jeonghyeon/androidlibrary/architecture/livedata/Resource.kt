package kim.jeonghyeon.androidlibrary.architecture.livedata

import androidx.lifecycle.MutableLiveData
import kim.jeonghyeon.androidlibrary.architecture.net.error.BaseError

data class Resource<T> constructor(val data: T?, val state: ResourceState) {

    companion object {
        fun <T> success(data: T?): Resource<T> = Resource(data, ResourceState.SUCCESS)

        fun <T> error(error: BaseError, data: T? = null) = Resource(data, ResourceState.error(error))

        fun <T> loading(data: T? = null): Resource<T> = Resource(data, ResourceState.LOADING)
    }

    fun asLiveData(): MutableLiveData<Resource<T>> =
            MutableLiveData<Resource<T>>().apply { value = this@Resource }
}

val Resource<*>?.status: ResourceStatus
    get() = this?.state?.status?:ResourceStatus.LOADING
val Resource<*>?.isLoading: Boolean
    get() = this?.state?.status ?: ResourceStatus.LOADING == ResourceStatus.LOADING

val Resource<*>?.isSuccess: Boolean
    get() = this?.state?.status ?: ResourceStatus.LOADING == ResourceStatus.SUCCESS

val Resource<*>?.isError: Boolean
    get() = this?.state?.status ?: ResourceStatus.LOADING == ResourceStatus.ERROR