package kim.jeonghyeon.client

import com.squareup.sqldelight.internal.Atomic
import kim.jeonghyeon.type.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*


typealias ViewModelFlow<T> = MutableSharedFlow<T>

/**
 * @param emitOnIdleOnly : if true, click event is ignored if already clicked and processing.
 */
fun <T> ViewModelFlow(emitOnIdleOnly: Boolean = false): ViewModelFlow<T> = MutableSharedFlow(1, 0, if (emitOnIdleOnly) BufferOverflow.DROP_LATEST else BufferOverflow.DROP_OLDEST )
fun <T> ViewModelFlow(initialValue: T, emitOnIdleOnly: Boolean = false): ViewModelFlow<T> = MutableSharedFlow<T>(1, 0, if (emitOnIdleOnly) BufferOverflow.DROP_LATEST else BufferOverflow.DROP_OLDEST)
    .apply {
        tryEmit(initialValue)
    }

/**
 * for empty value.
 */
fun ViewModelFlow<Unit>.call() {
    tryEmit(Unit)
}

var <T> ViewModelFlow<T>.value: T
    get(): T = replayCache[0]
    set(value) { tryEmit(value) }

val <T> ViewModelFlow<T>.valueOrNull get(): T? = replayCache.getOrNull(0)

/**
 * Progress.
 *
 * - Event not anymore required. as navigation is on viewModel.
 *  - StateFlow is enough on view side as there is no event.
 *  - but we have to consider animation like toast. the animation can run first time only. also should be lifecycle-aware of View. consider how other animation is handled.
 *      - this should be handled by UI side only. so, viewModel side doesn't know that.
 * - initialValue is decided on viewModel
 *  - but, what if the value is not nullable when it's collected? at first time, viewModel can't define the value as there is no value. so type should be nullable. but collector always have to ignore null value.
 *  - initialValue is decided on view side
 *      - reason
 *          - there is 3 cases for value
 *              - value not loaded
 *              - value not exists
 *              - value exists
 *          - if it's StateFlow, we can know that value already loaded(it can be null, but null is expected value), but there are cases on which value not yet loaded
 *          - so, to make code consistent, it's better to consider there are always 3 cases for value.
 *          - plus, if the value is Resource, Resource can be Loading, Error, etc, so, the required success value is able not to exist.
 *      - so, let's use Flow only.
 * - StateFlow is for UI side only
 * - how to retry on Repository side? retry is by Resource. Resource should contains retry methdo.
 * - it should be able to map to different flow easily on viewModel side.
 *
 * - the reason to use SharedFlow instead of StateFlow
 *      - StateFlow contains initialValue, and when map() or collect(), need to handle initialValue to ignore.
 *      - on ViewModel, there are various mapping is required.
 *      - as it should keep cache and shared.
 *
 *======================
 * TODO: migrate to SharedFlow when 1.4.0-M1-native-mt is released
 *  - All is covered by SharedFlow so, [DataFlow] is not required anymore
 *  - seems to add extension `val value: T?` and returns null if data not yet emitted
 *      - Reason
 *          - UI requires data at initial time to draw UI
 *          - but data not exists. so have to define initialValue
 *          - but need to decide which side set initialValue. viewModel side? or view side?
 *          - before decision, we have to consider event data.
 *              - event should be handled only when data is received.
 *              - if there is initialValue, we always have to ignore initialValue. and initialValue is not always null. but can be different value. so, view side always should know what is initialValue.
 *          - so, initialValue should be decided on View side.
 *          - we can say that Event and just Data is different. and make different field on ViewModel
 *              - but, same data can be used for event and data both. so the flow on ViewModel should support both
 *  - map to Resource with try catch is required
 *      - There is mention 'SharedFlow never completes'. but it doesn't means collect still working. when exception occurs, collect is not working.
 *      - seems to have to use MutableSharedFlow for retry on Error feature.
 *      - resourceFlow { } also required
 *
 *
 * =================== As it'll be migrated to SharedFlow, no need to read below. but explained why DataFlow was required===================
 * If I knew that SharedFlow support all. I wouldn't make DataFlow. 😢
 *
 * Flow on ViewModel !!Experimental!!
 * This is similar to LiveData. but difference is that setValue type is T instead of T?
 *
 * TODO
 *  - There are various operator because there is conversion among Flow, DataFlow, ResourceFlow.
 *  - this should get simpler.
 *  - Focus on the frequently used use case
 *
 * Reason to use [DataFlow] instead of [MutableStateFlow] (no. 4 is the biggest reason)
 * 1. Initial time value can not be empty. if the data should be fetched asynchronously, there can not be the data
 * 2. StateFlow is distinct.
 *   - There are two cases on view side.
 *      - event and state
 *      - event is like navigation. it should be called whenever value is set(but StateFlow doesn't emit if data is same)
 *      - state is like setting data on composable, it's not required to update if data is same.
 *      - one data can be used as event and state both. so it should be decide on View side, instead of ViewModel or Model
 *          (if logically distinct is required, we will do it on Model or ViewModel. but I'm talking about UI side data)
 * 3. [MutableStateFlow] is interface
 *  - as [MutableStateFlow] is Interface, xcode recognize value as Any.
 *  - if it's class, it's recognize as defined type
 * 4. it's difficult to use [map] operator from [StateFlow] to [StateFlow]. as [StateFlow] contains initial value, whenever use map, we have to add code to ignore empty value by developer as only developer knows that it's empty data or not.
 *
 * Characteristics
 * 1. on initial time, value can be empty(easy to handle empty case when use the data)
 * 2. getValue always nullable. as initial value is able to be empty(handle empty value on UI side)
 * 3. if value is not possible to be empty in context. use !! or set initialValue on UI side
 * 4. not distinct
 * 5. [mapData] operator to transform data and return value is [DataFlow]
 *    - as [DataFlow] can be collected by multiple collector, transforming between [Flow] to [DataFlow] should be independent from Collector's coroutine context.
 *    - so, use different [CoroutineScope]
 *    - TODO limitation : it's difficult to cancel transforming, as it's running in different scope.
 * 6. resource repeatable : normal flow can emit only from flow block. but by setValue, it's possible to retry same process
 *    - repeating is available by [Resource.retry]
 *    - when retry() is invoked, [DataFlow.repeat] is called or first resourceFlow block is repeated
 *    - so, if there are multiple transforming, [Resource.retry] calls last [DataFlow]'s repeat()
 *    - the below sample code show where the retry happen.
 *    val first = dataFlow(scope) {
 *      emit(1)
 *      emit(2)
 *    }
 *    first.map {
 *      it * it
 *    }.toDataFlow(scope) // when retry() called, this DataFlow.value is repeated.
 *    .mapToResource {
 *      api.getResult(it) // this api will be called with DataFlow's last value.
 *    }.mapResource {
 *      "result : $it"
 *    }.collect {
 *      it.retry()
 *    }
 *
 *    //or you can repeat whole process by the below
 *    first.repeat()
 * 7. ResourceFlow's Error or Loading can contains data if there was previous data
 * 8. when ResourceFlow start, Resource.Loading is emitted.
 */


/**
 * [Resource] is possible to retry,
 * for retrying, There should be DataFlow or it's first
 */
@OptIn(InternalCoroutinesApi::class)
fun <T, R> ViewModelFlow<T>.resourceMap(transformData: suspend (value: T) -> R): Flow<Resource<R>> =
    transform { value ->
        emitResource<R>(block = {
            emit(transformData(value))
        }, retry = {
            this@resourceMap.tryEmit(value)
        })
    }


private suspend fun <T> FlowCollector<Resource<T>>.emitResource(block: suspend FlowCollector<T>.() -> Unit, retry: () -> Unit) {
    emit(Resource.Loading {
        retry()
    })
    try {
        block(object : FlowCollector<T> {
            override suspend fun emit(value: T) {
                this@emitResource.emit(Resource.Success(value, retry))
            }
        })
    } catch (e: CancellationException) {
        //if cancel. then ignore it
    } catch (e: ResourceError) {
        emit(
            Resource.Error(e) {
                retry()
            }
        )
    } catch (e: Throwable) {
        emit(
            Resource.Error(UnknownResourceError(e)) {
                retry()
            }
        )
    }
}
