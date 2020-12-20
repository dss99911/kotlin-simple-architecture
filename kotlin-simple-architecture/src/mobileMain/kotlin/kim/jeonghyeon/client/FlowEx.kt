package kim.jeonghyeon.client

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.experimental.ExperimentalTypeInference

enum class FlowJobPolicy {
    /**
     * if transforming is busy. then upstream is ignored
     * if transforming is not busy but downstream is busy. upstream is accepted.
     */
    ON_IDLE,
    /**
     * if transforming is busy when upstream comes, then previous transforming get cancelled.
     * if transforming is not busy but downstream is busy, then downstream is not cancelled.
     */
    CANCEL_RUNNING
}


/**
 *
 * transform with [FlowJobPolicy]
 *
 * @param scope transformData is processed on this scope.
 *
 */
@OptIn(ExperimentalTypeInference::class)
inline fun <T, R> Flow<T>.transformWithJob(scope: CoroutineScope, jobPolicy: FlowJobPolicy,  @BuilderInference crossinline transformData: suspend FlowCollector<R>.(value: T) -> Unit): Flow<R> = flow {
    val channel = Channel<R>(Channel.RENDEZVOUS)
    var job: Job? = null
    scope.launch {
        collect {
            if (jobPolicy == FlowJobPolicy.CANCEL_RUNNING) {
                job?.cancel()
            }

            if (jobPolicy != FlowJobPolicy.ON_IDLE || job?.isActive != true) {
                job = scope.launch {
                    object : FlowCollector<R> {
                        override suspend fun emit(value: R) {
                            channel.send(value)
                        }
                    }.transformData(it)
                }
            }
        }
    }

    for (item in channel) {
        emit(item)
    }
}.shareIn(scope, SharingStarted.WhileSubscribed())

/**
 * refer to the doc [transformWithJob]
 */
inline fun <T, R> Flow<T>.mapWithJob(scope: CoroutineScope, jobPolicy: FlowJobPolicy, crossinline transformData: suspend (value: T) -> R): Flow<R> {
    return transformWithJob(scope, jobPolicy) {
        emit(transformData(it))
    }
}