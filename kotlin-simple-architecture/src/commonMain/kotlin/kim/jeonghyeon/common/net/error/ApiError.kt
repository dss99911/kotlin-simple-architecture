package kim.jeonghyeon.common.net.error

import io.ktor.http.HttpStatusCode

class ApiError(val body: ApiErrorBody, cause: Throwable? = null) :
    RuntimeException("${body.code}:${body.message}", cause)

//response body and error body is different. sever will devliver it different way.
//TODO HYUN [multi-platform2] : consider proguard on common module

private const val API_ERROR_CODE = 600

data class ApiErrorBody(
    val code: ApiErrorCode,
    val message: String?
)

fun HttpStatusCode.isApiError(): Boolean = value == API_ERROR_CODE

val HttpStatusCode.Companion.ApiError: HttpStatusCode
    get() = HttpStatusCode(
        API_ERROR_CODE,
        "Api Error"
    )

enum class ApiErrorCode {
    UNKNOWN,
    NO_NETWORK
}