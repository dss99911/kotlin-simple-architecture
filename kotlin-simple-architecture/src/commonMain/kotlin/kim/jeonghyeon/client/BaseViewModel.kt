package kim.jeonghyeon.client


import io.ktor.http.*
import kim.jeonghyeon.annotation.CallSuper
import kim.jeonghyeon.annotation.SimpleArchInternal
import kim.jeonghyeon.extension.fromJsonString
import kim.jeonghyeon.extension.toJsonStringNew
import kim.jeonghyeon.net.DeeplinkError
import kim.jeonghyeon.net.RedirectionType
import kim.jeonghyeon.type.AtomicReference
import kim.jeonghyeon.type.Resource
import kim.jeonghyeon.type.atomic
import kim.jeonghyeon.type.isLoading
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.reflect.KClass
import kotlin.reflect.KProperty


/**
 * don't use var property.
 *  - native freeze all field when background thread is running.
 * if some variable data is required. use [dataFlow] or [AtomicReference]
 */
open class BaseViewModel {
    companion object {
        val PARAM_NAME_PARAM = "param"
    }

    @SimpleArchInternal("used on IOS base code. don't use")
    val flows: MutableList<Lazy<DataFlow<*>>> = mutableListOf()

    val initStatus: StatusFlow by add { StatusFlow() }
    val status: StatusFlow by add { StatusFlow() }

    val isInitialized: AtomicReference<Boolean> = atomic(false)

    val scope: ViewModelScope by lazy { ViewModelScope() }

    val screenResult: DataFlow<ScreenResult> by add { DataFlow() }

    val eventGoBack: DataFlow<Unit> by add { DataFlow() }
    val eventToast: DataFlow<String> by add { DataFlow() }
    val eventDeeplink: DataFlow<DeeplinkNavigation> by add { DataFlow() }

    @SimpleArchInternal
    fun onCompose() {
        if (!isInitialized.getAndSet(true)) {
            handleDeeplink()
            onInitialized()
        }
    }

    /**
     * if need customizing handling deeplink. override this.
     * ex) show snackbar before navigation
     */
    open fun handleDeeplink() {
        initStatus.handleDeeplink()
        status.handleDeeplink()
    }

    /**
     * use this for custom resource to handle deeplink
     */
    fun ResourceFlow<*>.handleDeeplink() = collectOnViewModel { resource ->
        val deeplinkInfo =
            resource.errorOrNullOf<DeeplinkError>()?.deeplinkInfo ?: return@collectOnViewModel
        navigateToDeeplink(deeplinkInfo.url) {
            if (!it.isOk) {
                return@navigateToDeeplink
            }
            when (deeplinkInfo.redirectionInfo.type) {
                RedirectionType.retry -> {
                    resource.retry()
                }
                RedirectionType.redirectionUrl -> {
                    //todo it seems that,
                    eventDeeplink.call(DeeplinkNavigation(deeplinkInfo.redirectionInfo.url!!))
                }
                RedirectionType.none -> {
                    //do nothing(show error ui, and when user click retry button, call api again
                }
            }
        }
    }

    fun navigateToDeeplink(url: String, onResult: (ScreenResult) -> Unit = {}) {
        eventDeeplink.call(DeeplinkNavigation(url, object : DeeplinkResultListener {
            override fun onDeeplinkResult(result: ScreenResult) {
                onResult(result)
            }
        }))
    }

    fun navigateToDeeplink(
        url: String,
        vararg params: Any?,
        onResult: (ScreenResult) -> Unit = {}
    ) {
        val encodedUrl = URLBuilder(url).apply {
            params.forEachIndexed { index, data ->
                parameters.append(PARAM_NAME_PARAM + index, data.toJsonStringNew())
            }
        }.buildString()

        eventDeeplink.call(DeeplinkNavigation(encodedUrl, object : DeeplinkResultListener {
            override fun onDeeplinkResult(result: ScreenResult) {
                onResult(result)
            }
        }))
    }

    /**
     * when Screen is created, but not yet drawn. viewModel's init {} is invoked.
     * It's better to initialize data when Screen is drawn.
     */
    open fun onInitialized() {
    }

    @CallSuper
    fun onBackPressed() {
        if (screenResult.value == null) {
            screenResult.setValue(ScreenResult(ScreenResult.RESULT_CODE_CANCEL))
        }
        onCleared()
    }

    /**
     * this is sometimes not called directly on ios
     */
    @CallSuper
    open fun onCleared() {
        scope.close()
    }

    fun <T> Flow<T>.toDataFlow(): DataFlow<T> =
        toDataFlow(scope)

    fun <T> Flow<Resource<T>>.toDataFlow(statusFlow: StatusFlow): DataFlow<T> =
        toDataFlow(scope, statusFlow)

    fun <T> dataFlow(block: suspend DataFlow<T>.() -> Unit): DataFlow<T> =
        dataFlow(scope, block)

    fun <T> resourceFlow(block: suspend FlowCollector<T>.() -> Unit): ResourceFlow<T> =
        resourceFlow(scope, block)


    open fun onDeeplinkReceived(url: Url) {

    }

    fun goBack() {
        eventGoBack.call()
    }

    fun goBackWithOk(data: Any? = null) {
        goBack(ScreenResult(ScreenResult.RESULT_CODE_OK, data))
    }

    fun goBack(result: ScreenResult) {
        this.screenResult.setValue(result)
        goBack()
    }

    fun toast(message: String) {
        eventToast.call(message)
    }

    fun <T> ResourceFlow<T>.load(work: suspend CoroutineScope.() -> T) {
        scope.loadResource(this, work)
    }

    fun <T> ResourceFlow<T>.loadWithStatus(
        status: StatusFlow,
        work: suspend CoroutineScope.() -> T
    ) {
        scope.loadResource(this, status, work)
    }

    fun <T> DataFlow<T>.load(status: StatusFlow, work: suspend CoroutineScope.() -> T) {
        scope.loadDataAndStatus(this, status, work)
    }

    fun <T, U> DataFlow<U>.load(
        status: StatusFlow,
        work: suspend CoroutineScope.() -> T,
        transform: suspend CoroutineScope.(Resource<T>) -> Resource<U>
    ) {
        scope.loadDataAndStatus(this, status, work, transform = transform)
    }

    fun <T> ResourceFlow<T>.loadInIdle(work: suspend CoroutineScope.() -> T) {
        if (value.isLoading()) {
            return
        }
        scope.loadResource(this, status, work)
    }

    fun <T> loadInIdle(work: suspend CoroutineScope.() -> T) {
        status.loadInIdle(work)
    }

    //todo even if source is cold stream, the source get active directly, even if DataFlow is not active
    fun <T, U> DataFlow<T>.withSource(
        source: Flow<U>,
        onCollect: DataFlow<T>.(U) -> Unit
    ): DataFlow<T> {
        source.collectOnViewModel {
            onCollect(this@withSource, it)
        }
        return this
    }

    fun <T> DataFlow<T>.withSource(
        source: Flow<T>
    ): DataFlow<T> {
        source.collectOnViewModel {
            setValue(it)
        }
        return this
    }


    @SimpleArchInternal("used on IOS base code. don't use these code")
    val initialized: Boolean
        get() = isInitialized.value

    @SimpleArchInternal("used on IOS base code. don't use these code")
    fun watchChanges(action: () -> Unit) {
        flows.forEach {
            it.value.collectOnViewModel {
                action()
            }
        }
    }

    fun <T> Flow<T>.collectOnViewModel(action: suspend (value: T) -> Unit) {
        scope.launch {
            collect(action)
        }
    }

    inline fun <reified T : Any?> Url.getParam(index: Int): T? =
        parameters[PARAM_NAME_PARAM + index]?.fromJsonString<T>()

    inline fun <reified T : Any> Url.getParam(index: Int, @Suppress("UNUSED_PARAMETER") type: KClass<T>): T? =
        parameters[PARAM_NAME_PARAM + index]?.fromJsonString<T>()

    /**
     * this is used because ios should keep flows to watch changes.
     * when create flow, use only this.
     *
     * the reason to add additional function instead helper function like dataFlow()
     * is that, DataFlow can be transformed. and can't be sure which DataFlow will be collected by View side.
     * so, use this function to the flow which is used by View side
     *
     */
    fun <T> add(initializer: () -> DataFlow<T>): Lazy<DataFlow<T>> =
        lazy(initializer).also {
            flows.add(it)
        }

}

data class ScreenResult(val resultCode: Int, val data: Any? = null) {
    companion object {
        val RESULT_CODE_OK = 1
        val RESULT_CODE_CANCEL = 0
    }

    val isOk get() = resultCode == RESULT_CODE_OK
    val isCancel get() = resultCode == RESULT_CODE_CANCEL

    @Suppress("UNCHECKED_CAST")
    fun <T> dataOf(): T = data as T

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> dataOf(@Suppress("UNUSED_PARAMETER") type: KClass<T>): T = data as T
}

data class DeeplinkNavigation(val url: String, val resultListener: DeeplinkResultListener? = null)

interface DeeplinkResultListener {
    fun onDeeplinkResult(result: ScreenResult)
}