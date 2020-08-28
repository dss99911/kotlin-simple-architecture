package kim.jeonghyeon.util

actual class Logger actual constructor() {
    actual inline fun i(message: String) {
        println("[INFO] $message")
    }
    actual inline fun i(vararg obj: Any?) {
        println("[INFO] ${obj.toString()}")
    }
    actual inline fun d(message: String) {
        println("[DEBUG] $message")
    }
    actual inline fun d(vararg obj: Any?) {
        println("[DEBUG] ${obj.toString()}")
    }
    actual inline fun e(e: Throwable) {
        println("[ERROR] ${e.message}")
    }
    actual inline fun e(e: String) {
        println("[ERROR] $e")
    }
    actual inline fun e(vararg obj: Any?) {
        println("[ERROR] ${obj.toString()}")
    }
}