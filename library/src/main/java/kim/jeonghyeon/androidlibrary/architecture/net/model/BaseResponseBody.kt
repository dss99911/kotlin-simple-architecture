package kim.jeonghyeon.androidlibrary.architecture.net.model

data class BaseResponseBody<T>(
    val code: Int,
    val message: String? = null,
    val data: T
) {
    fun isSuccess(): Boolean =
        code == ResponseCodeConstants.SUCCESS
}