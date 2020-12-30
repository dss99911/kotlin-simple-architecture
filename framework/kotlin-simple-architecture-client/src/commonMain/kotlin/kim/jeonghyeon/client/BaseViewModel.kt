@file:Suppress("unused", "MemberVisibilityCanBePrivate", "EXPERIMENTAL_API_USAGE")

package kim.jeonghyeon.client


import io.ktor.http.*
import kim.jeonghyeon.annotation.SimpleArchInternal
import kim.jeonghyeon.net.DeeplinkError
import kim.jeonghyeon.net.RedirectionType
import kim.jeonghyeon.type.*
import kim.jeonghyeon.util.log
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

    open val initStatus: ViewModelFlow<Status> = viewModelFlow()
    open val status: ViewModelFlow<Status> = viewModelFlow()

    open val title: String = ""

    val isInitialized: AtomicReference<Boolean> = atomic(false)
    private val _initFlow: ViewModelFlow<Unit> = viewModelFlow()
    val initFlow: Flow<Unit> = _initFlow

    val scope: ViewModelScope by lazy { ViewModelScope() }

    val screenResult: ViewModelFlow<ScreenResult> = viewModelFlow()

    /**
     * todo Support Toast.
     *  as it should be shown when screen is appeared.
     *  so, change text to null on ui side.
     *  as toast is shown on all screen instead of showing only one screen.
     *  this seems to have to be collected by BaseActivity.
     */
    val toastText: ViewModelFlow<String> = viewModelFlow()

    //todo collect this, and root screen ignore back button event.
    val canGoBack: ViewModelFlow<Boolean> = viewModelFlow(true)
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
        val deeplinkInfo = resource.errorOrNullOf<DeeplinkError>()?.deeplinkInfo ?: return@collectOnViewModel

        launch {
            val result = navigateToDeeplinkForResult(deeplinkInfo.url)
            log.i("[handleDeeplink] $result, $deeplinkInfo")
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

    fun <T> viewModelFlow(): ViewModelFlow<T> = viewModelFlow<T>(this)
    fun <T> viewModelFlow(initialValue: T): ViewModelFlow<T> = viewModelFlow<T>(this, initialValue)


    fun launch(block: suspend CoroutineScope.() -> Unit): Job = scope.launch(block = block)

    fun <T> Flow<T>.assign(result: MutableSharedFlow<Resource<T>>) {
        assign(scope, result)
    }

    fun <T> Flow<T>.assign(data: MutableSharedFlow<T>, status: MutableSharedFlow<Status>) {
        assign(scope, data, status)
    }

    fun <T> MutableSharedFlow<Resource<T>>.load(work: suspend CoroutineScope.() -> T) {
        loadResource(scope) {
            scope.work()
        }
    }

    fun <T> ViewModelFlow<T>.load(status: ViewModelFlow<Status>, work: suspend CoroutineScope.() -> T) {
        loadResource(scope, status) {
            scope.work()
        }
    }

    fun <T> ViewModelFlow<Resource<T>>.loadWithStatus(
        status: MutableSharedFlow<Status>,
        work: suspend CoroutineScope.() -> T
    ) {
        loadResourceWithStatus(scope, status) {
            scope.work()
        }
    }

    fun <T> ViewModelFlow<Resource<T>>.loadInIdle(work: suspend CoroutineScope.() -> T) {
        if (valueOrNull.isLoading()) {
            return
        }
        load {
            scope.work()
        }
    }

    fun <T> ViewModelFlow<T>.loadInIdle(status: ViewModelFlow<Status>, work: suspend CoroutineScope.() -> T) {
        if (status.valueOrNull.isLoading()) {
            return
        }
        load(status) {
            scope.work()
        }
    }

    fun <T> loadInIdle(work: suspend CoroutineScope.() -> T) {
        status.loadInIdle(work)
    }

    fun <T> ViewModelFlow<Resource<T>>.loadDebounce(delayMillis: Long, work: suspend CoroutineScope.() -> T) {
        valueOrNull?.onLoading {cancel, _ ->
            cancel()
        }

        load {
            delay(delayMillis)
            scope.work()
        }
    }

    fun <T> ViewModelFlow<T>.loadDebounce(delayMillis: Long, status: ViewModelFlow<Status>, work: suspend CoroutineScope.() -> T) {
        status.valueOrNull?.onLoading {cancel, _ ->
            cancel()
        }

        load(status) {
            delay(delayMillis)
            scope.work()
        }
    }

    fun <T> loadDebounce(delayMillis: Long, work: suspend CoroutineScope.() -> T) {
        status.loadDebounce(delayMillis, work)
    }

    fun <T> Flow<T>.toData(status: MutableSharedFlow<Status>? = null): ViewModelFlow<T> = toData(this@BaseViewModel, scope, status)

    fun <T> Flow<T>.toResource(): ViewModelFlow<Resource<T>> = toResource(this@BaseViewModel, scope)

    fun <T> Flow<T>.toStatus(): ViewModelFlow<Status> = toStatus(this@BaseViewModel, scope)

    @OptIn(ExperimentalTypeInference::class)
    inline fun <T, U> ViewModelFlow<T>.withSource(
        source: Flow<U>,
        @BuilderInference crossinline transform: suspend FlowCollector<T>.(value: U) -> Unit
    ): ViewModelFlow<T> = ViewModelFlow(this@BaseViewModel, withSource(scope, source, transform))

    fun <T> ViewModelFlow<T>.withSource(
        source: Flow<T>
    ): ViewModelFlow<T> = ViewModelFlow(this@BaseViewModel, withSource(scope, source))

    fun <T, R> Flow<T>.mapInIdle(transformData: suspend (value: T) -> R): Flow<R> = mapInIdle(transformData)

    fun <T, R> Flow<T>.mapCancelRunning(transformData: suspend (value: T) -> R): Flow<R> = mapCancelRunning(scope, transformData)

    @OptIn(ExperimentalTypeInference::class)
    fun <T, R> Flow<T>.transformInIdle(@BuilderInference transformData: suspend FlowCollector<R>.(value: T) -> Unit): Flow<R> = transformInIdle(scope, transformData)

    @OptIn(ExperimentalTypeInference::class)
    fun <T, R> Flow<T>.transformCancelRunning(@BuilderInference transformData: suspend FlowCollector<R>.(value: T) -> Unit): Flow<R> = transformCancelRunning(scope, transformData)

    fun <T> Flow<T>.collectOnViewModel(action: suspend (value: T) -> Unit) {
        launch {
            collect(action)
        }
    }

    @SimpleArchInternal
    fun onCompose() {
        if (!isInitialized.getAndSet(true)) {
            handleDeeplink()
            _initFlow.call()
            onInitialized()
        }
    }

    @SimpleArchInternal("used on IOS base code. don't use these code")
    val initialized: Boolean
        get() = isInitialized.value
    @SimpleArchInternal("used on IOS base code. don't use these code")
    val changeCount = viewModelFlow(0)

    @SimpleArchInternal("used on IOS base code. don't use these code")
    val isWatched: AtomicReference<Boolean> = atomic(false)

    @SimpleArchInternal("used on IOS base code. don't use these code")
    fun watchChanges(action: (Any?) -> Unit): ViewModelScope {
        //each screen is created whenever screen is changed. even viewModel already exists.
        //so, coroutineScope should follow Screen's lifecycle
        //so, onAppear, create scope.
        //onDisappear, close the scope.
        val screenScope = ViewModelScope()
        screenScope.launch {
            changeCount.collect {
                action(it)
            }
        }
        return screenScope
    }

    @SimpleArchInternal("used on IOS base code. don't use these code")
    val flowSet = atomic(setOf<ViewModelFlow<*>>())

    /**
     * as this is not recognized on swift. let BaseViewModel to refer this.
     */
    @SimpleArchInternal("used on IOS base code. don't use these code")
    val iosUiManager: UiManager get() {
        error("don't call this")
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