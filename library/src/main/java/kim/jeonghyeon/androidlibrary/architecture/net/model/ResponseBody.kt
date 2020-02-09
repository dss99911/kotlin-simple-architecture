package kim.jeonghyeon.androidlibrary.architecture.net.model

data class ResponseBody<T>(
    val code: Int,
    val message: String? = null,
    val data: T
)

fun <T> ResponseBody<T>?.isSuccess(): Boolean =
    this != null && code == ResponseCodeConstants.SUCCESS