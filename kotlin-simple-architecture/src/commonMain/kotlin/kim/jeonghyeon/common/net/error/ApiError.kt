package kim.jeonghyeon.common.net.error

import io.ktor.http.HttpStatusCode
import kotlinx.serialization.Serializable

class ApiError(val body: ApiErrorBody, cause: Throwable? = null) :
    RuntimeException("${body.code}:${body.message}", cause)

//response body and error body is different. sever will devliver it different way.
//TODO HYUN [multi-platform2] : consider proguard on common module

private const val HTTP_STATUS_CODE_API_ERROR = 600

@Serializable
data class ApiErrorBody(
    val code: Int = CODE_UNKNOWN,
    val message: String?
) {
    companion object {
        const val CODE_UNKNOWN = 9999
        const val CODE_NO_NETWORK = 1000
    }
}

fun HttpStatusCode.isApiError(): Boolean = value == HTTP_STATUS_CODE_API_ERROR

val HttpStatusCode.Companion.ApiError: HttpStatusCode
    get() = HttpStatusCode(
        HTTP_STATUS_CODE_API_ERROR,
        "Api Error"
    )