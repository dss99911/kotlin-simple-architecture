package kim.jeonghyeon.client

import kim.jeonghyeon.type.CFlow
import kim.jeonghyeon.type.asCFlow
import kotlinx.coroutines.flow.MutableStateFlow

//todo consider to delete this on 1.4
abstract class BaseViewModelIos {

    abstract val viewModel: BaseViewModel

    val flows: MutableList<Lazy<CFlow<*>>> = mutableListOf()

    val initStatus by cflow { viewModel.initStatus }
    val status by cflow { viewModel.status }
    val isInitialized get() = viewModel.isInitialized.value

    fun forEachFlow(action: (CFlow<*>) -> Unit) {
        flows.forEach { action(it.value) }
    }

    fun onAppear() {
        viewModel.onCompose()
    }

    open fun onCleared() {
        viewModel.onCleared()
    }

    fun <T> cflow(flow: () -> MutableStateFlow<T>): Lazy<CFlow<T>> = lazy {
        flow().asCFlow(viewModel.scope)
    }
        .also { flows.add(it) }


}