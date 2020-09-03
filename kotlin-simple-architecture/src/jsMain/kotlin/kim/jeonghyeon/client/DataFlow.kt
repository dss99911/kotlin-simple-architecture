package kim.jeonghyeon.client

import kotlinx.coroutines.flow.MutableStateFlow

/**
 * as [MutableStateFlow] is Interface,
 * xcode recognize value as Any.
 * if it's class, it's recognize as defined type
 */
//todo after fix https://github.com/Kotlin/kotlinx.coroutines/issues/2226
// delete this
actual class DataFlow<T> actual constructor(value: T) : MutableStateFlow<T> by MutableStateFlow(value)