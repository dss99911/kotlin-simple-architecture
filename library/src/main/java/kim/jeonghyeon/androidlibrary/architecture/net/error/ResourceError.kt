package kim.jeonghyeon.androidlibrary.architecture.net.error

import androidx.lifecycle.MutableLiveData
import kim.jeonghyeon.androidlibrary.architecture.livedata.Resource
import kim.jeonghyeon.androidlibrary.architecture.livedata.asLiveData

open class ResourceError(cause: Throwable? = null) : RuntimeException(cause) {
    fun asResource(): Resource<Nothing> = Resource.Error(this)
    fun <T> asResourceLiveData(): MutableLiveData<Resource<T>> = asResource().asLiveData()
}