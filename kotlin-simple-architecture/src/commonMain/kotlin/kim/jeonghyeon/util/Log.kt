package kim.jeonghyeon.util

@Suppress("NOTHING_TO_INLINE")
expect inline fun log(e: Throwable)

@Suppress("NOTHING_TO_INLINE")
expect inline fun log(message: String)

@Suppress("NOTHING_TO_INLINE")
expect inline fun log(vararg obj: Any?)