package kim.jeonghyeon.androidlibrary.architecture.livedata

import kim.jeonghyeon.androidlibrary.architecture.net.error.BaseError


enum class ResourceStatus {
    SUCCESS, ERROR, LOADING
}

/**
 * on the error state, you can retry
 */
class ResourceState private constructor(
        val status: ResourceStatus,
        val error: BaseError? = null) {
    companion object {
        val SUCCESS = ResourceState(ResourceStatus.SUCCESS)
        val LOADING = ResourceState(ResourceStatus.LOADING)
        fun error(error: BaseError) = ResourceState(ResourceStatus.ERROR, error)
    }
}