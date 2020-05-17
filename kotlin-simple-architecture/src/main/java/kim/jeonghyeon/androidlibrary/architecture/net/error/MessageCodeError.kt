package kim.jeonghyeon.androidlibrary.architecture.net.error

open class MessageCodeError(val code: Int, errorMessage: String) :
    MessageError("$code : $errorMessage")