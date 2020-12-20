package kim.jeonghyeon.client

import kim.jeonghyeon.type.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlin.experimental.ExperimentalTypeInference


/**
 * most commonly used flow
 *
 * - single cached shared flow.
 * - drop oldest
 */
fun <T> flowSingle(): MutableSharedFlow<T> = MutableSharedFlow(1, 0, BufferOverflow.DROP_OLDEST)
fun <T> flowSingle(initialValue: T): MutableSharedFlow<T> = MutableSharedFlow<T>(1, 0, BufferOverflow.DROP_OLDEST)
    .apply {
        tryEmit(initialValue)
    }

/**
 * for empty value.
 */
fun MutableSharedFlow<Unit>.call() {
    tryEmit(Unit)
}

var <T> MutableSharedFlow<T>.value: T
    get(): T = replayCache[0]
    set(value) { tryEmit(value) }

val <T> MutableSharedFlow<T>.valueOrNull get(): T? = replayCache.getOrNull(0)
