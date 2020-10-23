@file:Suppress("unused", "MemberVisibilityCanBePrivate", "EXPERIMENTAL_API_USAGE")

package kim.jeonghyeon.client


import io.ktor.http.*
import kim.jeonghyeon.annotation.SimpleArchInternal
import kim.jeonghyeon.net.DeeplinkError
import kim.jeonghyeon.net.RedirectionType
import kim.jeonghyeon.type.AtomicReference
import kim.jeonghyeon.type.Resource
import kim.jeonghyeon.type.atomic
import kim.jeonghyeon.type.isLoading
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.reflect.KClass


/**
 * don't use var property.
 *  - native freeze all field when background thread is running.
 * if some variable data is required. use [dataFlow] or [AtomicReference]
 *
 * Todo [KSA-140] Support SavedState on Android
 *  only configured data or flow will be saved and restored.
 *  this is same approach with existing savedStateHandler
 */
open class BaseViewModel {

    @SimpleArchInternal("used on IOS base code. don't use")
    val flows: MutableList<Lazy<DataFlow<*>>> = mutableListOf()

    open val initStatus: StatusFlow by add { StatusFlow() }
    open val status: StatusFlow by add { StatusFlow() }

    /**
     * todo consider to merge [isInitialized], [initFlow]
     */
    val isInitialized: AtomicReference<Boolean> = atomic(false)
    val initFlow by add { DataFlow<Unit>() }

    /**
     * todo delete?
     * This is used only for deeplink function
     * If deeplink is mapped with root ViewModel. app doesn't [navigate] new viewModel. but [Navigator.backUpToRoot]
     * If you want to add the root viewModel on top of current viewModel. then set this false
     */
    open val isRoot: Boolean = false

    val scope: ViewModelScope by lazy { ViewModelScope() }

    val screenResult: DataFlow<ScreenResult> by add { DataFlow() }

    /**
     * if it's shown, the value is changed to null
     */
    val toastText: DataFlow<String?> by add { DataFlow() }

    //todo collect this, and root screen ignore back button event.
    val canGoBack: DataFlow<Boolean> by add { DataFlow(true) }

    @SimpleArchInternal
    fun onCompose() {
        if (!isInitialized.getAndSet(true)) {
            handleDeeplink()
            initFlow.call()
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

        launch {
            val result = navigateToDeeplinkForResult(deeplinkInfo.url)
            if (!result.isOk) {
                return@launch
            }
            when (deeplinkInfo.redirectionInfo.type) {
                RedirectionType.retry -> {
                    resource.retry()
                }
                RedirectionType.redirectionUrl -> {
                    navigateToDeeplink(deeplinkInfo.redirectionInfo.url!!)
                }
                RedirectionType.none -> {
                    //do nothing(show error ui, and when user click retry button, call api again
                }
            }
        }
    }

    fun navigate(viewModel: BaseViewModel): Boolean = Navigator.navigate(viewModel)

    //todo this doesn't support savedState.
    // for navigation with supporting savedState, add different mechanism.
    // but, as this is simple than the mechanism. if the savedState is not required, just use this.
    suspend fun navigateForResult(viewModel: BaseViewModel): ScreenResult = suspendCoroutine { continuation ->
        viewModel.screenResult.collectOnViewModel {
            continuation.resume(it)
        }
        if (!navigate(viewModel)) {
            continuation.resumeWithException(IllegalStateException("can't navigate to same ViewModel"))
        }
    }

    fun navigateToDeeplink(url: String) {
        with(DeeplinkNavigator) {
            navigateToDeeplinkFromInternal(DeeplinkNavigation(url, null))
        }
    }

    suspend fun navigateToDeeplinkForResult(url: String): ScreenResult = suspendCoroutine { continuation ->
        with(DeeplinkNavigator) {
            navigateToDeeplinkFromInternal(DeeplinkNavigation(url, object : DeeplinkResultListener {
                override fun onDeeplinkResult(result: ScreenResult) {
                    continuation.resume(result)
                }
            }))
        }
    }

    /**
     * when Screen is created, but not yet drawn. viewModel's init {} is invoked.
     * It's better to initialize data when Screen is drawn.
     */
    open fun onInitialized() {}

    fun onBackPressed() {
        if (screenResult.value == null) {
            screenResult.setValue(ScreenResult(ScreenResult.RESULT_CODE_CANCEL))
        }
        clear()
    }

    /**
     * this is sometimes not called directly on ios
     */
    open fun onCleared() {

    }

    fun clear() {
        scope.close()
        onCleared()
    }

    fun <T> Flow<T>.toDataFlow(): DataFlow<T> =
        toDataFlow(scope)

    fun <T> Flow<Resource<T>>.toResourceFlow(): ResourceFlow<T> =
        toResourceFlow(scope)

    fun <T> Flow<Resource<T>>.toDataFlow(statusFlow: StatusFlow): DataFlow<T> =
        toDataFlow(scope, statusFlow)

    fun <T> dataFlow(block: suspend DataFlow<T>.() -> Unit): DataFlow<T> =
        dataFlow(scope, block)

    fun <T> resourceFlow(block: suspend FlowCollector<T>.() -> Unit): ResourceFlow<T> =
        resourceFlow(scope, block)

    fun <T, R> Flow<T>.mapToResource(transformData: suspend (value: T) -> R): Flow<Resource<R>> = mapToResource(scope, transformData)

    fun <T, R> Flow<T>.mapToResourceIfIdle(transformData: suspend (value: T) -> R): Flow<Resource<R>> = mapToResourceIfIdle(scope, transformData)

    open fun onDeeplinkReceived(url: Url) {
    }

    fun goBack() {
        Navigator.backUpTo(this, true)
    }

    fun goBackWithOk(data: Any? = null) {
        goBack(ScreenResult(ScreenResult.RESULT_CODE_OK, data))
    }

    fun goBack(result: ScreenResult) {
        this.screenResult.setValue(result)
        goBack()
    }

    fun toast(message: String) {
        toastText.call(message)
    }

    fun launch(block: suspend CoroutineScope.() -> Unit): Job = scope.launch(block = block)

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

    fun <T> DataFlow<T>.loadInIdle(status: StatusFlow, work: suspend CoroutineScope.() -> T) {
        if (status.value.isLoading()) {
            return
        }
        scope.loadDataAndStatus(this, status, work)
    }

    fun <T> loadInIdle(work: suspend CoroutineScope.() -> T) {
        status.loadInIdle(work)
    }

    fun <T> ResourceFlow<T>.loadDebounce(delayMillis: Long, work: suspend CoroutineScope.() -> T) {
        value?.onLoading { _, cancel ->
            cancel()
        }
        load {
            delay(delayMillis)
            work()
        }
    }

    fun <T> DataFlow<T>.loadDebounce(statusFlow: StatusFlow, delayMillis: Long, work: suspend CoroutineScope.() -> T) {
        statusFlow.value?.onLoading { _, cancel ->
            cancel()
        }
        load(statusFlow) {
            delay(delayMillis)
            work()
        }
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
        launch {
            collect(action)
        }
    }

    /**
     * this is used because ios should keep flows to watch changes.
     * when create flow, use only this.
     *
     * the reason to add additional function instead helper function like dataFlow()
     * is that, DataFlow can be transformed. and can't be sure which DataFlow will be collected by View side.
     * so, use this function to the flow which is used by View side
     *
     * todo is there simpler way?
     */
    fun <T> add(initializer: () -> DataFlow<T>): Lazy<DataFlow<T>> =
        lazy(initializer).also {
            flows.add(it)
        }

}

data class ScreenResult(val resultCode: Int, val data: Any? = null) {
    companion object {
        const val RESULT_CODE_OK = 1
        const val RESULT_CODE_CANCEL = 0
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