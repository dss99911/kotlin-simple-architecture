package kim.jeonghyeon.androidlibrary.util

import kotlinx.coroutines.delay

/**
 * @param action returns data and not polling again. but if exception occurs, polling
 */
suspend inline fun <T> polling(
    count: Int,
    initialDelay: Long,
    delayMillis: Long,
    action: (index: Int) -> T
): T {
    delay(initialDelay)
    repeat(count) { repeatIndex ->
        try {
            return action(repeatIndex)
        } catch (e: Exception) {
            //retry
            delay(delayMillis)
        }
    }
    throw PollingException()
}

class PollingException : RuntimeException()