package kim.jeonghyeon.client


import io.ktor.http.*
import kim.jeonghyeon.annotation.CallSuper
import kim.jeonghyeon.annotation.SimpleArchInternal
import kim.jeonghyeon.type.AtomicReference
import kim.jeonghyeon.type.Resource
import kim.jeonghyeon.type.Status
import kim.jeonghyeon.type.atomic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@OptIn(ExperimentalCoroutinesApi::class)
typealias StatusFlow = DataFlow<Status>
@OptIn(ExperimentalCoroutinesApi::class)
typealias ResourceFlow<T> = DataFlow<Resource<T>>

open class BaseViewModel {

    @SimpleArchInternal("used on IOS base code. don't use")
    val flows: MutableList<MutableStateFlow<*>> = mutableListOf()

    val initStatus: StatusFlow = statusFlow()
    val status: StatusFlow = statusFlow()

    val isInitialized: AtomicReference<Boolean> = atomic(false)

    val scope: ViewModelScope by lazy { ViewModelScope() }

    @OptIn(ExperimentalCoroutinesApi::class)
    val eventGoBack = dataFlow<Unit?>(null)
    @OptIn(ExperimentalCoroutinesApi::class)
    val eventToast = dataFlow<String?>(null)

    @SimpleArchInternal
    fun onCompose() {
        if (!isInitialized.getAndSet(true)) {
            onInitialized()
        }
    }

    open fun onInitialized() {
    }

    @CallSuper
    open fun onCleared() {
        scope.close()
    }

    /**
     * this is used because ios should keep flows to watch changes.
     * when create flow, use only this
     */
    fun <T> dataFlow(value: T): DataFlow<T> {
        return DataFlow(value).also {
            flows.add(it)
        }
    }

    /**
     * this is used because ios should keep flows to watch changes.
     * when create flow, use only this
     */
    fun <T> resourceFlow(resource: Resource<T> = Resource.Start): ResourceFlow<T> {
        return ResourceFlow(resource).also {
            flows.add(it)
        }
    }

    /**
     * this is used because ios should keep flows to watch changes.
     * when create flow, use only this
     */
    fun statusFlow(resource: Status = Resource.Start): StatusFlow {
        return StatusFlow(resource).also {
            flows.add(it)
        }
    }

    open fun onDeeplinkReceived(url: Url) {

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun goBack() {
        eventGoBack.value = Unit
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun toast(message: String) {
        eventToast.value = message
    }

    fun <T> ResourceFlow<T>.load(work: suspend CoroutineScope.() -> T) {
        scope.loadResource(this, work)
    }

    fun <T> ResourceFlow<T>.loadWithStatus(status: StatusFlow, work: suspend CoroutineScope.() -> T) {
        scope.loadResource(this, status, work)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun <T> DataFlow<T>.load(status: StatusFlow, work: suspend CoroutineScope.() -> T) {
        scope.loadDataAndStatus(this, status, work)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun <T, U> DataFlow<U>.load(status: StatusFlow, work: suspend CoroutineScope.() -> T, transform: suspend CoroutineScope.(Resource<T>) -> Resource<U>) {
        scope.loadDataAndStatus(this, status, work, transform = transform)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun <T> ResourceFlow<T>.loadInIdle(work: suspend CoroutineScope.() -> T) {
        if (value.isLoading()) {
            return
        }
        scope.loadResource(this, status, work)
    }

    fun <T> loadInIdle(work: suspend CoroutineScope.() -> T) {
        status.loadInIdle(work)
    }

    fun <T> ResourceFlow<T>.load(flow: Flow<Resource<T>>) {
        scope.loadFlow(this, null, flow)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun <T> DataFlow<T>.load(status: StatusFlow, flow: Flow<Resource<T>>) {
        scope.loadResourceFromFlow(this, status, flow)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun <T, U> DataFlow<U>.load(status: StatusFlow, flow: Flow<Resource<T>>, transform: suspend CoroutineScope.(Resource<T>) -> Resource<U>) {
        scope.loadResourceFromFlow(this, status, flow, transform = transform)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun <T> DataFlow<T>.loadFlow(status: StatusFlow, flow: Flow<T>) {
        scope.loadDataFromFlow(this, status, flow)
    }

    fun <T, U> DataFlow<T>.withSource(
        source: MutableStateFlow<U>,
        onCollect: MutableStateFlow<T>.(U) -> Unit
    ): DataFlow<T> {
        scope.launch {
            source.collect {
                onCollect(this@withSource, it)
            }
        }
        return this
    }


    @SimpleArchInternal("used on IOS base code. don't use these code")
    val initialized: Boolean get() = isInitialized.value

    @SimpleArchInternal("used on IOS base code. don't use these code")
    fun watchChanges(action: () -> Unit) {
        flows.forEach {
            scope.launch {
                it.collect {
                   action()
                }
            }
        }
    }

}

