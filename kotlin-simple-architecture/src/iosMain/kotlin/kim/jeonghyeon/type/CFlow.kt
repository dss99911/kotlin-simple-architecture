package kim.jeonghyeon.type

import kim.jeonghyeon.client.dispatcherViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

fun <T> MutableStateFlow<T>.asCFlow(scope: CoroutineScope = CoroutineScope(dispatcherViewModel())): CFlow<T> = CFlow(scope, this)

open class CFlow<T>(private val scope: CoroutineScope, private val origin: MutableStateFlow<T>) : Flow<T> by origin {
    @InternalCoroutinesApi
    open fun watch(block: (T) -> Unit) {
        scope.launch {
            collect {
                block(it)
            }
        }
    }

    var value: T
        get() = origin.value
        set(value) {
            origin.value = value
        }
}