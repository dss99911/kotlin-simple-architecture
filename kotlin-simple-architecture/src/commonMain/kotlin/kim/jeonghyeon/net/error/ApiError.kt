package kim.jeonghyeon.net.error

import io.ktor.http.HttpStatusCode
import kim.jeonghyeon.net.DeeplinkError
import kim.jeonghyeon.net.DeeplinkInfo
import kim.jeonghyeon.type.ResourceError
import kotlinx.serialization.Serializable

/**
 * @param body this is delivered to client
 */
class ApiError(val body: ApiErrorBody, cause: Throwable? = null) :
    ResourceError("${body.code}:${body.message}", cause) {
    val code get() = body.code
    val errorMessage get() = body.message
}

fun Exception.isApiErrorOf(expectedBody: ApiErrorBody): Boolean = this is ApiError && body == expectedBody

fun errorApi(code: Int, message: String? = null, cause: Throwable? = null): Nothing {
    throw ApiError(ApiErrorBody(code, message), cause)
}

fun errorApi(body: ApiErrorBody, cause: Throwable? = null): Nothing {
    throw ApiError(body, cause)
}

fun errorDeeplink(info: DeeplinkInfo, cause: Throwable? = null): Nothing {
    throw DeeplinkError(info, cause)
}

//response body and error body is different. sever will devliver it different way.
//TODO HYUN [multi-platform2] : consider proguard on common module

private const val HTTP_STATUS_CODE_API_ERROR = 299//if it's not in success bound, ktor client throw exception. so that can't get error body.
private const val HTTP_STATUS_CODE_DEEPLINK_ERROR = 298//if it's not in success bound, ktor client throw exception. so that can't get error body.

/**
 * 1~999 error code includes [HttpStatusCode].
 * 1000~1999 error code include library side error
 * 2000~ recommend to define custom error
 */
@Serializable
data class ApiErrorBody(
    val code: Int = CODE_UNKNOWN,
    val message: String?
) {
    override fun equals(other: Any?): Boolean {
        return code == (other as? ApiErrorBody)?.code
    }

    override fun hashCode(): Int {
        return code
    }

    companion object {
        const val CODE_UNKNOWN = 9999
        val Unknown = ApiErrorBody(CODE_UNKNOWN, "Unknown Error")
        val NoNetwork = ApiErrorBody(1000, "Netowrk Error")
        val credentialInvalid = ApiErrorBody(1002, "ID or Password incorrect")
        val idAlreadyExists = ApiErrorBody(1003, "ID exists")
        val invalidSignUpRequest = ApiErrorBody(1004, "Sign Up Request Rejected")
        
        //////http status codes///////

        val Continue = ApiErrorBody(100, "Continue")
        val SwitchingProtocols = ApiErrorBody(101, "Switching Protocols")
        val Processing = ApiErrorBody(102, "Processing")

        val OK = ApiErrorBody(200, "OK")
        val Created = ApiErrorBody(201, "Created")
        val Accepted = ApiErrorBody(202, "Accepted")
        val NonAuthoritativeInformation = ApiErrorBody(203, "Non-Authoritative Information")
        val NoContent = ApiErrorBody(204, "No Content")
        val ResetContent = ApiErrorBody(205, "Reset Content")
        val PartialContent = ApiErrorBody(206, "Partial Content")
        val MultiStatus = ApiErrorBody(207, "Multi-Status")

        val MultipleChoices = ApiErrorBody(300, "Multiple Choices")
        val MovedPermanently = ApiErrorBody(301, "Moved Permanently")
        val Found = ApiErrorBody(302, "Found")
        val SeeOther = ApiErrorBody(303, "See Other")
        val NotModified = ApiErrorBody(304, "Not Modified")
        val UseProxy = ApiErrorBody(305, "Use Proxy")
        val SwitchProxy = ApiErrorBody(306, "Switch Proxy")
        val TemporaryRedirect = ApiErrorBody(307, "Temporary Redirect")
        val PermanentRedirect = ApiErrorBody(308, "Permanent Redirect")

        val BadRequest = ApiErrorBody(400, "Bad Request")
        val Unauthorized = ApiErrorBody(401, "Unauthorized")
        val PaymentRequired = ApiErrorBody(402, "Payment Required")
        val Forbidden = ApiErrorBody(403, "Forbidden")
        val NotFound = ApiErrorBody(404, "Not Found")
        val MethodNotAllowed = ApiErrorBody(405, "Method Not Allowed")
        val NotAcceptable = ApiErrorBody(406, "Not Acceptable")
        val ProxyAuthenticationRequired = ApiErrorBody(407, "Proxy Authentication Required")
        val RequestTimeout = ApiErrorBody(408, "Request Timeout")
        val Conflict = ApiErrorBody(409, "Conflict")
        val Gone = ApiErrorBody(410, "Gone")
        val LengthRequired = ApiErrorBody(411, "Length Required")
        val PreconditionFailed = ApiErrorBody(412, "Precondition Failed")
        val PayloadTooLarge = ApiErrorBody(413, "Payload Too Large")
        val RequestURITooLong = ApiErrorBody(414, "Request-URI Too Long")

        val UnsupportedMediaType = ApiErrorBody(415, "Unsupported Media Type")
        val RequestedRangeNotSatisfiable = ApiErrorBody(416, "Requested Range Not Satisfiable")
        val ExpectationFailed = ApiErrorBody(417, "Expectation Failed")
        val UnprocessableEntity = ApiErrorBody(422, "Unprocessable Entity")
        val Locked = ApiErrorBody(423, "Locked")
        val FailedDependency = ApiErrorBody(424, "Failed Dependency")
        val UpgradeRequired = ApiErrorBody(426, "Upgrade Required")
        val TooManyRequests = ApiErrorBody(429, "Too Many Requests")
        val RequestHeaderFieldTooLarge = ApiErrorBody(431, "Request Header Fields Too Large")

        val InternalServerError = ApiErrorBody(500, "Internal Server Error")
        val NotImplemented = ApiErrorBody(501, "Not Implemented")
        val BadGateway = ApiErrorBody(502, "Bad Gateway")
        val ServiceUnavailable = ApiErrorBody(503, "Service Unavailable")
        val GatewayTimeout = ApiErrorBody(504, "Gateway Timeout")
        val VersionNotSupported = ApiErrorBody(505, "HTTP Version Not Supported")
        val VariantAlsoNegotiates = ApiErrorBody(506, "Variant Also Negotiates")
        val InsufficientStorage = ApiErrorBody(507, "Insufficient Storage")
    }
}

fun HttpStatusCode.isApiError(): Boolean = value == HTTP_STATUS_CODE_API_ERROR
fun HttpStatusCode.isDeeplinkError(): Boolean = value == HTTP_STATUS_CODE_DEEPLINK_ERROR

val HttpStatusCode.Companion.ApiError: HttpStatusCode get() = HttpStatusCode(HTTP_STATUS_CODE_API_ERROR, "Api Error")
val HttpStatusCode.Companion.DeeplinkError: HttpStatusCode get() = HttpStatusCode(HTTP_STATUS_CODE_DEEPLINK_ERROR, "Deeplink Error")