package kim.jeonghyeon.coroutine

import kim.jeonghyeon.type.Resource
import kim.jeonghyeon.type.ResourceError
import kim.jeonghyeon.type.UnknownResourceError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlin.experimental.ExperimentalTypeInference


/**
 * able to retry manually
 * on error, able to retry.
 *
 * if we implement retry on error on viewModel side.
 *  viewModel should take care if the flow is singleton or factory
 *  because viewModel side implementation create new flow on retry.
 *  but, if use this way, no need to take care of that.
 */
@OptIn(ExperimentalTypeInference::class)
fun <T> resourceFlow(@BuilderInference block: suspend FlowCollector<T>.(retry: () -> Unit) -> Unit): Flow<Resource<T>> = flow {
    observeTrial { retry ->
        try {
            block(object: FlowCollector<T> {
                override suspend fun emit(value: T) {
                    this@flow.emit(Resource.Success(value))
                }
            }, retry)
        } catch (e: CancellationException) {
            //if cancel. then ignore it
        } catch (e: ResourceError) {
            emit(Resource.Error<T>(e, retry = retry))
        } catch (e: Throwable) {
            emit(Resource.Error<T>(UnknownResourceError(e), retry = retry))
        }

    }
}

suspend fun observeTrial(onRepeat: suspend (retry: () -> Unit) -> Unit) {

    val channel = Channel<Unit>(Channel.CONFLATED)
    channel.offer(Unit)

    try {
        for (item in channel) {
            onRepeat {
                channel.offer(Unit)
            }
        }
    } finally {
    }
}