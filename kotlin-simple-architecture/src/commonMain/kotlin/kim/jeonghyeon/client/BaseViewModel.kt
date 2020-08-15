package kim.jeonghyeon.client


import kim.jeonghyeon.annotation.CallSuper
import kim.jeonghyeon.type.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect

typealias StatusStateFlow = MutableStateFlow<Status>
typealias ResourceStateFlow<T> = MutableStateFlow<Resource<T>>

fun StatusStateFlow(status: Status = Resource.Start): StatusStateFlow = MutableStateFlow(status)
fun <T> ResourceStateFlow(resource: Resource<T> = Resource.Start): ResourceStateFlow<T> = MutableStateFlow(resource)

open class BaseViewModel {

    val initStatus: StatusStateFlow = StatusStateFlow()
    val status: StatusStateFlow = StatusStateFlow()

    val isInitialized: AtomicReference<Boolean> = atomic(false)

    val scope: ViewModelScope by lazy { ViewModelScope() }

    val eventGoBack = MutableStateFlow<Unit?>(null)
    val eventToast = MutableStateFlow<String?>(null)

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

    fun goBack() {
        eventGoBack.value = Unit
    }

    fun toast(message: String) {
        eventToast.value = message
    }

    fun <T> ResourceStateFlow<T>.load(work: suspend CoroutineScope.() -> T) {
        scope.loadResource(this, work)
    }

    fun <T> ResourceStateFlow<T>.loadWithStatus(status: StatusStateFlow, work: suspend CoroutineScope.() -> T) {
        scope.loadResource(this, status, work)
    }

    fun <T> MutableStateFlow<T>.load(status: StatusStateFlow, work: suspend CoroutineScope.() -> T) {
        scope.loadDataAndStatus(this, status, work)
    }

    fun <T, U> MutableStateFlow<U>.load(status: StatusStateFlow, work: suspend CoroutineScope.() -> T, transform: suspend CoroutineScope.(Resource<T>) -> Resource<U>) {
        scope.loadDataAndStatus(this, status, work, transform = transform)
    }

    fun <T> ResourceStateFlow<T>.loadInIdle(work: suspend CoroutineScope.() -> T) {
        if (value.isLoading()) {
            return
        }
        scope.loadResource(this, status, work)
    }

    fun <T> loadInIdle(work: suspend CoroutineScope.() -> T) {
        status.loadInIdle(work)
    }

    fun <T> ResourceStateFlow<T>.loadFlow(flow: () -> Flow<T>) {
        scope.loadFlow(this, null, flow)
    }

    fun <T> MutableStateFlow<T>.loadFlow(status: StatusStateFlow, flow: () -> Flow<T>) {
        scope.loadDataFromFlow(this, status, flow)
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

