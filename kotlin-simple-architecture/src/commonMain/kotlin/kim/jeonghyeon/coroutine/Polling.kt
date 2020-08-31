package kim.jeonghyeon.coroutine

import kim.jeonghyeon.type.ResourceError
import kim.jeonghyeon.util.log
import kotlinx.coroutines.delay

/**
 * don't use this after other async(), if the async() error occrus, this should be stopped but because of try catch, this is not stopped.
 * @param action returns data and not polling again. but if exception occurs, polling
 * polling fail can be handled by [PollingError] on view side
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
            log.e(e)
            //retry
            delay(delayMillis)
        }
    }
    throw PollingError()
}

class PollingError : ResourceError()