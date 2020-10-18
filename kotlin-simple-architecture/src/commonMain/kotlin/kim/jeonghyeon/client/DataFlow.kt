package kim.jeonghyeon.client

import kim.jeonghyeon.type.*
import kim.jeonghyeon.util.log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*


/**
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
@OptIn(ExperimentalCoroutinesApi::class)
open class DataFlow<T>
constructor() : Flow<T> {
    constructor(initialValue: T) : this() {
        setValue(initialValue)
    }

    private object Empty

    private val version = MutableStateFlow(0)
    private val _state = atomic<Any?>(Empty)
    private val initialized = atomic(false)

    @Suppress("UNCHECKED_CAST")
    val value: T?
        get() = if (_state.value is Empty) {
            null
        } else _state.value as T

    fun setValue(value: T) {
        _state.value = value
        version.value += 1
    }

    /**
     * if it's event data, word 'call' seems better.
     */
    fun call(value: T) {
        setValue(value)
    }

    /**
     * on first [collect] invoked
     * this is used for cold stream
     */
    open fun onActive() {

    }

    /**
     * repeat stream.
     */
    fun repeat() {
        setValue(value?: return)
    }

    @OptIn(InternalCoroutinesApi::class)
    override suspend fun collect(collector: FlowCollector<T>) {
        if (!initialized.getAndSet(true)) {
            onActive()
        }

        version.collect collect2@{
            //as MutableStateFlow empty-less, ignore value if [setValue] is not invoked
            if (it == 0) {
                return@collect2
            }

            @Suppress("UNCHECKED_CAST")
            collector.emit(value as T)
        }
    }

    /**
     * used in ios
     */
    fun watch(scope: CoroutineScope, perform: (T) -> Unit) {
        scope.launch {
            collect {
                perform(it)
            }
        }
    }
}

suspend fun <T : Any> DataFlow<T?>.collectNotNull(onCollect: (T) -> Unit) {
    collect {
        if (it != null) {
            onCollect(it)
        }
    }
}

fun DataFlow<Unit>.call() {
    setValue(Unit)
}

fun <T> Flow<T>.toDataFlow(scope: CoroutineScope): DataFlow<T> =
    dataFlow(scope) {
        this@toDataFlow.collect {
            setValue(it)
        }
    }

fun <T> Flow<Resource<T>>.toResourceFlow(scope: CoroutineScope): ResourceFlow<T> =
    dataFlow(scope) {
        this@toResourceFlow.collect {
            setNewValue(it)
        }
    }

/**
 * [block] is invoked only one time.
 * todo as toResourceFlow require DataFlow, block's receiver is DataFlow. but FlowCollector is general way to use.
 */
fun <T> dataFlow(scope: CoroutineScope, block: suspend DataFlow<T>.() -> Unit): DataFlow<T> =
    object : DataFlow<T>() {
        override fun onActive() {
            scope.launch {
                block()
            }
        }
    }


fun <T> resourceFlow(scope: CoroutineScope, block: suspend FlowCollector<T>.() -> Unit): ResourceFlow<T> =
    object : ResourceFlow<T>() {
        override fun onActive() {
            scope.launch {
                object : FlowCollector<Resource<T>> {
                    override suspend fun emit(value: Resource<T>) {
                        setNewValue(value)
                    }
                }.emitResource(block) {
                    onActive()
                }
            }
        }
    }

/**
 * [Resource] is possible to retry,
 * for retrying, There should be DataFlow or it's first
 */
@OptIn(InternalCoroutinesApi::class)
fun <T, R> DataFlow<T>.mapToResource(transformData: suspend (value: T) -> R): Flow<Resource<R>> =
    transform<T, Resource<R>> { value ->
        emitResource(block = {
            emit(transformData(value))
        }, retry = {
            this@mapToResource.repeat()
        })
    }

/**
 * ignore collected value if it's busy
 *
 * todo how about using busy(), idle() operator with coroutineScope. is coroutineScope is delivered throw multiple flow?
 *  if busy(), ignore new value.
 *  if idle(), allow new value.
 */
fun <T, R> Flow<T>.mapToResourceIfIdle(scope: CoroutineScope, transformData: suspend (value: T) -> R): Flow<Resource<R>> {
    var processing = false
    val middleStream = dataFlow<T>(scope) {
        this@mapToResourceIfIdle.collect {
            if (!processing) {
                setValue(it)
            }
        }
    }

    return middleStream.mapToResource {
        processing = true
        transformData(it).also {
            processing = false
        }
    }
}

@OptIn(InternalCoroutinesApi::class)
fun <T, R> Flow<T>.mapToResource(scope: CoroutineScope, transformData: suspend (value: T) -> R): Flow<Resource<R>> = toDataFlow(scope)
        .mapToResource(transformData)

fun <T, R> Flow<Resource<T>>.mapResource(transformData: suspend (value: T) -> R): Flow<Resource<R>> =
    transform<Resource<T>, Resource<R>> { resource ->
        when (resource) {
            is Resource.Success -> {
                Resource.Success(transformData(resource.data())) {
                    resource.retry()
                }
            }
            is Resource.Loading -> {
                Resource.Loading {
                    resource.retry()
                }
            }
            is Resource.Error -> {
                Resource.Error(resource.errorData) {
                    resource.retry()
                }
            }
        }
    }

fun <T> Flow<Resource<T>>.toDataFlow(scope: CoroutineScope, statusFlow: StatusFlow): DataFlow<T> =
    dataFlow(scope) {
        this@toDataFlow.collect { value ->
            if (!value.isDataEmpty()) {
                setValue(value.data())
            }
            statusFlow.setValue(value)
        }
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

/**
 * if previously there was data on ResourceFlow<T>, then even when error occur, data should be kept on Resource.
 * so, set newValue but set previous data
 */
private fun <T> ResourceFlow<T>.setNewValue(newValue: Resource<T>) {
    @Suppress("NullableBooleanElvis")
    if (value?.isDataEmpty()?:true) {
        setValue(newValue)
        return
    }
    setValue(when (newValue) {
        is Resource.Error -> {
            newValue.copy(last = value!!.data())
        }
        is Resource.Loading -> {
            newValue.copy(last = value!!.data())
        }
        else -> newValue
    })
}

/**
 * if a flow's value is affected by multiple other flow.
 * merge other flows
 */
@OptIn(ExperimentalCoroutinesApi::class)
inline fun <T> flowsToSingle(
    vararg flows: Flow<T>,
): Flow<T> = channelFlow {
    flows.forEach {
        launch {
            it.collect {
                send(it)
            }
        }
    }

}
typealias StatusFlow = DataFlow<Status>
typealias ResourceFlow<T> = DataFlow<Resource<T>>