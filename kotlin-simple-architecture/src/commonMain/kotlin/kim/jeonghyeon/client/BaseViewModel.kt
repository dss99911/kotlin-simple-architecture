package kim.jeonghyeon.client


import io.ktor.http.*
import kim.jeonghyeon.annotation.CallSuper
import kim.jeonghyeon.annotation.SimpleArchInternal
import kim.jeonghyeon.type.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect


@OptIn(ExperimentalCoroutinesApi::class)
typealias StatusStateFlow = MutableStateFlow<Status>
@OptIn(ExperimentalCoroutinesApi::class)
typealias ResourceStateFlow<T> = MutableStateFlow<Resource<T>>

@OptIn(ExperimentalCoroutinesApi::class)
fun StatusStateFlow(status: Status = Resource.Start): StatusStateFlow = MutableStateFlow(status)
@OptIn(ExperimentalCoroutinesApi::class)
fun <T> ResourceStateFlow(resource: Resource<T> = Resource.Start): ResourceStateFlow<T> = MutableStateFlow(resource)

open class BaseViewModel {

    val initStatus: StatusStateFlow = StatusStateFlow()
    val status: StatusStateFlow = StatusStateFlow()

    val isInitialized: AtomicReference<Boolean> = atomic(false)

    val scope: ViewModelScope by lazy { ViewModelScope() }

    @OptIn(ExperimentalCoroutinesApi::class)
    val eventGoBack = MutableStateFlow<Unit?>(null)
    @OptIn(ExperimentalCoroutinesApi::class)
    val eventToast = MutableStateFlow<String?>(null)

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

    fun <T> ResourceStateFlow<T>.load(work: suspend CoroutineScope.() -> T) {
        scope.loadResource(this, work)
    }

    fun <T> ResourceStateFlow<T>.loadWithStatus(status: StatusStateFlow, work: suspend CoroutineScope.() -> T) {
        scope.loadResource(this, status, work)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun <T> MutableStateFlow<T>.load(status: StatusStateFlow, work: suspend CoroutineScope.() -> T) {
        scope.loadDataAndStatus(this, status, work)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun <T, U> MutableStateFlow<U>.load(status: StatusStateFlow, work: suspend CoroutineScope.() -> T, transform: suspend CoroutineScope.(Resource<T>) -> Resource<U>) {
        scope.loadDataAndStatus(this, status, work, transform = transform)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun <T> ResourceStateFlow<T>.loadInIdle(work: suspend CoroutineScope.() -> T) {
        if (value.isLoading()) {
            return
        }
        scope.loadResource(this, status, work)
    }

    fun <T> loadInIdle(work: suspend CoroutineScope.() -> T) {
        status.loadInIdle(work)
    }

    //todo delete?
    fun <T> ResourceStateFlow<T>.loadFlow(flow: () -> Flow<T>) {
        scope.loadFlow(this, null, flow)
    }

    //todo delete?
    @OptIn(ExperimentalCoroutinesApi::class)
    fun <T> MutableStateFlow<T>.loadFlow(status: StatusStateFlow, flow: () -> Flow<T>) {
        scope.loadDataFromFlow(this, status, flow)
    }

    fun <T> ResourceStateFlow<T>.load(flow: Flow<Resource<T>>) {
        scope.loadFlow2(this, null, flow)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun <T> MutableStateFlow<T>.load(status: StatusStateFlow, flow: Flow<Resource<T>>) {
        scope.loadDataFromFlow(this, status, flow)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun <T, U> MutableStateFlow<U>.load(status: StatusStateFlow, flow: Flow<Resource<T>>, transform: suspend CoroutineScope.(Resource<T>) -> Resource<U>) {
        scope.loadDataFromFlow(this, status, flow, transform = transform)
    }

    fun <T, U> MutableStateFlow<T>.withSource(
        source: MutableStateFlow<U>,
        onCollect: MutableStateFlow<T>.(U) -> Unit
    ): MutableStateFlow<T> {
        scope.launch {
            source.collect {
                onCollect(this@withSource, it)
            }
        }
        return this
    }

}

