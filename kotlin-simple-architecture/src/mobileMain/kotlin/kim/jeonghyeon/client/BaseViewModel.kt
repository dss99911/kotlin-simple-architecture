@file:Suppress("unused", "MemberVisibilityCanBePrivate", "EXPERIMENTAL_API_USAGE")

package kim.jeonghyeon.client


import io.ktor.http.*
import kim.jeonghyeon.annotation.SimpleArchInternal
import kim.jeonghyeon.net.DeeplinkError
import kim.jeonghyeon.net.RedirectionType
import kim.jeonghyeon.type.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.experimental.ExperimentalTypeInference
import kotlin.reflect.KClass

/**
 * don't use var property.
 *  - native freeze all field when background thread is running.
 * if some variable data is required. use [Flow] or [AtomicReference]
 *
 * Todo [KSA-140] Support SavedState on Android
 *  only configured data or flow will be saved and restored.
 *  this is same approach with existing savedStateHandler
 */
open class BaseViewModel {

    @SimpleArchInternal("used on IOS base code. don't use")
    val flows: MutableList<Lazy<Flow<*>>> = mutableListOf()

    open val initStatus: MutableSharedFlow<Status> by add { flowSingle() }
    open val status: MutableSharedFlow<Status> by add { flowSingle() }

    open val title: String = ""

    val isInitialized: AtomicReference<Boolean> = atomic(false)
    val initFlow by add { flowSingle<Unit>() }

    val scope: ViewModelScope by lazy { ViewModelScope() }

    val screenResult: MutableSharedFlow<ScreenResult> by add { flowSingle() }

    /**
     * todo Support Toast.
     *  as it should be shown when screen is appeared.
     *  so, change text to null on ui side.
     *  as toast is shown on all screen instead of showing only one screen.
     *  this seems to have to be collected by BaseActivity.
     */
    val toastText: MutableSharedFlow<String> by add { flowSingle() }

    //todo collect this, and root screen ignore back button event.
    val canGoBack: MutableSharedFlow<Boolean> by add { flowSingle(true) }

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
    fun Flow<Resource<*>>.handleDeeplink() = collectOnViewModel { resource ->
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
        if (screenResult.valueOrNull == null) {
            screenResult.value = ScreenResult(ScreenResult.RESULT_CODE_CANCEL)
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

    open fun onDeeplinkReceived(url: Url) {
    }

    fun goBack() {
        Navigator.backUpTo(this, true)
    }

    fun goBackWithOk(data: Any? = null) {
        goBack(ScreenResult(ScreenResult.RESULT_CODE_OK, data))
    }

    fun goBack(result: ScreenResult) {
        this.screenResult.value = result
        goBack()
    }

    fun toast(message: String) {
        toastText.value = message
    }

    fun launch(block: suspend CoroutineScope.() -> Unit): Job = scope.launch(block = block)

    fun <T> MutableSharedFlow<Resource<T>>.load(work: suspend CoroutineScope.() -> T) {
        scope.loadResource(this, work)
    }

    fun <T> MutableSharedFlow<Resource<T>>.loadWithStatus(
        status: MutableSharedFlow<Status>,
        work: suspend CoroutineScope.() -> T
    ) {
        scope.loadResource(this, status, work)
    }

    fun <T> MutableSharedFlow<T>.load(status: MutableSharedFlow<Status>, work: suspend CoroutineScope.() -> T) {
        scope.loadDataAndStatus(this, status, work)
    }

    fun <T, U> MutableSharedFlow<U>.load(
        status: MutableSharedFlow<Status>,
        work: suspend CoroutineScope.() -> T,
        transform: suspend CoroutineScope.(Resource<T>) -> Resource<U>
    ) {
        scope.loadDataAndStatus(this, status, work, transform = transform)
    }

    fun <T> MutableSharedFlow<Resource<T>>.loadInIdle(work: suspend CoroutineScope.() -> T) {
        if (valueOrNull.isLoading()) {
            return
        }
        scope.loadResource(this, status, work)
    }

    fun <T> MutableSharedFlow<T>.loadInIdle(status: MutableSharedFlow<Status>, work: suspend CoroutineScope.() -> T) {
        if (status.value.isLoading()) {
            return
        }
        scope.loadDataAndStatus(this, status, work)
    }

    fun <T> loadInIdle(work: suspend CoroutineScope.() -> T) {
        status.loadInIdle(work)
    }

    fun <T> MutableSharedFlow<Resource<T>>.loadDebounce(delayMillis: Long, work: suspend CoroutineScope.() -> T) {
//        valueOrNull?.onLoading { _, cancel ->
//            cancel()
//        }
        load {
            delay(delayMillis)
            work()
        }
    }

    fun <T> MutableSharedFlow<T>.loadDebounce(statusFlow: MutableSharedFlow<Status>, delayMillis: Long, work: suspend CoroutineScope.() -> T) {
//        statusFlow.valueOrNull?.onLoading { _, cancel ->
//            cancel()
//        }
        load(statusFlow) {
            delay(delayMillis)
            work()
        }
    }

    //todo even if source is cold stream, the source get active directly, even if DataFlow is not active
    @OptIn(ExperimentalTypeInference::class)
    inline fun <T, U> MutableSharedFlow<T>.withSource(
        source: Flow<U>,
        @BuilderInference crossinline transform: suspend FlowCollector<T>.(value: U) -> Unit
    ): MutableSharedFlow<T> {
        subscriptionCount
            .takeWhile { it > 0 }
            .onEach {
                launch {
                    emitAll(source.transform(transform))
                }

                currentCoroutineContext().cancel()
            }.launchIn(scope)

        return this
    }

    fun <T> MutableSharedFlow<T>.withSource(
        source: Flow<T>
    ): MutableSharedFlow<T> = withSource(source) {
        emit(it)
    }


    @SimpleArchInternal("used on IOS base code. don't use these code")
    val initialized: Boolean
        get() = isInitialized.value

    @SimpleArchInternal("used on IOS base code. don't use these code")
    val isWatched: AtomicReference<Boolean> = atomic(false)

    @SimpleArchInternal("used on IOS base code. don't use these code")
    fun watchChanges(action: (Any?) -> Unit): ViewModelScope {
        //each screen is created whenever screen is changed. even viewModel already exists.
        //so, coroutineScope should follow Screen's lifecycle
        //so, onAppear, create scope.
        //onDisappear, close the scope.
        val screenScope = ViewModelScope()
        flows.forEach { dataFlow ->
            screenScope.launch {
                dataFlow.value.collect {
                    action(it)
                }
            }
        }
        return screenScope
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
    fun <T : Flow<*>> add(initializer: () -> T): Lazy<T> =
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