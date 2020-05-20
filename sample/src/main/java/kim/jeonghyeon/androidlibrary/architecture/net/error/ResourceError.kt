package kim.jeonghyeon.androidlibrary.architecture.net.error

import kim.jeonghyeon.androidlibrary.architecture.livedata.LiveResource
import kim.jeonghyeon.androidlibrary.architecture.livedata.Resource
import kim.jeonghyeon.androidlibrary.architecture.livedata.asLive

open class ResourceError(message: String? = null, cause: Throwable? = null) :
    RuntimeException(message, cause) {
    fun asResource(): Resource<Nothing> = Resource.Error(this)
    fun <T> asLiveResource(): LiveResource<T> = asResource().asLive()
}