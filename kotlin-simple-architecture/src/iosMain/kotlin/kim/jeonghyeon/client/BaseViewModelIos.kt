package kim.jeonghyeon.client

import kim.jeonghyeon.type.CFlow
import kim.jeonghyeon.type.asCFlow
import kotlinx.coroutines.flow.MutableStateFlow

//todo consider to delete this on 1.4
abstract class BaseViewModelIos {
    abstract val viewModel: BaseViewModel

    val initStatus by cflow { viewModel.initStatus }
    val status by cflow { viewModel.status }

    abstract val flows: Array<CFlow<*>>

    fun forEachFlow(action: (CFlow<*>) -> Unit) {
        action(initStatus)
        action(status)

        flows.forEach {
            action(it)
        }
    }

    fun onAppear() {
        viewModel.onCompose()
    }

    val isInitialized get() = viewModel.isInitialized.value

    open fun onCleared() {
        viewModel.onCleared()
    }

    fun <T> cflow(flow: () -> MutableStateFlow<T>): Lazy<CFlow<T>> = lazy { flow().asCFlow(viewModel.scope) }
}