package kim.jeonghyeon.coroutine

import kim.jeonghyeon.client.DataFlow
import kim.jeonghyeon.client.call
import kim.jeonghyeon.type.Resource
import kim.jeonghyeon.type.ResourceError
import kim.jeonghyeon.type.UnknownResourceError
import kim.jeonghyeon.type.atomic
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.experimental.ExperimentalTypeInference


/**
 * 1. retriable : when data is changed. it's possible to call again.
 * 2. cacheable : when there are multiple collector and if it's already called, doesn't call and just use previous one
 * 3. resource : catch exceptions
 *
 * as DataFlow and ResourceFlow is stateful(has state)
 * so named this with only retriable as ResourceFlow contains meaning of state.
 *
 * todo the method to use retry() is not good.
 *  find way to retry gracefully. like flow.retry()
 *  but this contains stateFlow, and just flow both. so, it looks not easy
 */
@OptIn(ExperimentalTypeInference::class)
fun <T> retriableResourceFlow(@BuilderInference block: suspend FlowCollector<T>.(retry: () -> Unit) -> Unit): Flow<Resource<T>> {
    val startFlow = DataFlow(Unit)

    val retry = {
        startFlow.call(Unit)
    }

    return startFlow.transform<Unit, Resource<T>> {
        //if using catch operator of Flow, collect() is completed. so, used try catch.
        try {
            block(object : FlowCollector<T> {
                override suspend fun emit(value: T) {
                    emit(Resource.Success(value))
                }
            }, retry)
        } catch (e: CancellationException) {
            //if cancel. then ignore it
        } catch (e: ResourceError) {
            emit(Resource.Error(e, retry = retry))
        } catch (e: Throwable) {
            emit(Resource.Error(UnknownResourceError(e), retry = retry))
        }
    }.cacheable()
}

/**
 * Flow is collected by MainScope.
 * because, this can be collected by multiple collector
 * so, Flow should be collected by independent scope
 * Todo Limit : when returned DataFlow's collect is cancelled, can't cancel Flow.
 *
 */
fun <T> Flow<T>.cacheable(): Flow<T> {
    val state = DataFlow<Any?>(None)
    val initialized = atomic(false)

    return flow {
        //call api on first collect happened.
        if (!initialized.getAndSet(true)) {
            //this should be independent against each collect(). so, used different scope
            MainScope().launch {
                collect { value ->
                    state.value = value
                }
            }
        }

        state.collect {
            if (it != None) {
                @Suppress("UNCHECKED_CAST")
                emit(it as T)
            }
        }
    }
}

private object None

