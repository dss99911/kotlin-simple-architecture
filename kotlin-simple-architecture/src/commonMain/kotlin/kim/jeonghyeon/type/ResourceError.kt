package kim.jeonghyeon.type

open class ResourceError(message: String? = null, cause: Throwable? = null) :
    RuntimeException(message, cause) {
    fun asResource(): Resource<Nothing> = Resource.Error(this)
}