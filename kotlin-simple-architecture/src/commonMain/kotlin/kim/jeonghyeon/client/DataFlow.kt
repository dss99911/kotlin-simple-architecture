package kim.jeonghyeon.client

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * as [MutableStateFlow] is Interface,
 * xcode recognize value as Any.
 * if it's class, it's recognize as defined type
 */
class DataFlow<T>(value: T) : MutableStateFlow<T> by MutableStateFlow(value) {
    /**
     * used in ios
     */
    fun watch(scope: CoroutineScope, perform: (T) -> Unit) {
        scope.launch {
            collect {
                perform(it)
            }
        }

    }
}
