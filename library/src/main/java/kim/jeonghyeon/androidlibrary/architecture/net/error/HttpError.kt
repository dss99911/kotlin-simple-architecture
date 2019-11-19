package kim.jeonghyeon.androidlibrary.architecture.net.error

open class HttpError(code: Int, errorMessage: String): MessageCodeError(code, errorMessage)