package kim.jeonghyeon.coroutine

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

/**
 * @param condition : if true, cancel
 * @param onCancel : on cancelled
 */
suspend fun <T> Flow<T>.collectAndCancel(condition: suspend (data: T) -> Boolean, onCancel: suspend () -> Unit) {
    try {
        coroutineScope {
            collect {
                if (condition(it)) {
                    this@coroutineScope.cancel(ManualCancellationException())
                }
            }
        }
    } catch (e: ManualCancellationException) {
        onCancel()
    }
}

private class ManualCancellationException : CancellationException(null)