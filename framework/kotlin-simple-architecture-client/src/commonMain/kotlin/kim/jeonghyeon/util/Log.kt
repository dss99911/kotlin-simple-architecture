package kim.jeonghyeon.util


val log = Logger()

expect class Logger() {
    inline fun i(message: String)
    inline fun i(vararg obj: Any?)
    inline fun d(message: String)
    inline fun d(vararg obj: Any?)
    inline fun e(e: Throwable)
    inline fun e(e: String)
    inline fun e(vararg obj: Any?)
}