package kim.jeonghyeon.common.net.error

import io.ktor.http.HttpStatusCode
import kotlinx.serialization.Serializable

class ApiError(body: ApiErrorBody, cause: Throwable? = null) : RuntimeException("${body.code}:${body.message}", cause)

//response body and error body is different. sever will devliver it different way.
//TODO HYUN [multi-platform2] : consider proguard on common module
data class ApiErrorBody(
    val code: ApiErrorCode,
    val message: String
)

fun HttpStatusCode.isApiError(): Boolean = value == 600

enum class ApiErrorCode {
    UNKNOWN,
    NO_NETWORK
}