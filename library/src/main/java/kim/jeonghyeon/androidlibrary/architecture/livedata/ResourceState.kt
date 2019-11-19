package kim.jeonghyeon.androidlibrary.architecture.livedata

import kim.jeonghyeon.androidlibrary.architecture.net.error.ResourceError


enum class ResourceStatus {
    SUCCESS, ERROR, LOADING
}

/**
 * on the error state, you can retry
 */
class ResourceState private constructor(
        val status: ResourceStatus,
        val error: ResourceError? = null) {
    companion object {
        val SUCCESS = ResourceState(ResourceStatus.SUCCESS)
        val LOADING = ResourceState(ResourceStatus.LOADING)
        fun error(error: ResourceError) = ResourceState(ResourceStatus.ERROR, error)
    }
}