package kim.jeonghyeon.util


@Suppress("NOTHING_TO_INLINE")
actual inline fun log(e: Throwable) {
    e.printStackTrace()
}

@Suppress("NOTHING_TO_INLINE")
actual inline fun log(message: String) {
    println(message)
}

@Suppress("NOTHING_TO_INLINE")
actual inline fun log(vararg obj: Any?) {
    println(obj.toString())
}