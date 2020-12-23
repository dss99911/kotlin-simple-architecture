package kim.jeonghyeon.client

import kim.jeonghyeon.type.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlin.experimental.ExperimentalTypeInference





//
//
//@OptIn(ExperimentalTypeInference::class)
//inline fun <T, U> MutableSharedFlow<T>.withSource(
//    scope: CoroutineScope,
//    source: Flow<U>,
//    @BuilderInference crossinline transform: suspend FlowCollector<T>.(value: U) -> Unit
//): MutableSharedFlow<T> = onActive(scope) {
//    if (it) {
//        scope.launch {
//            emitAll(source.transform(transform))
//        }
//        currentCoroutineContext().cancel()
//    }
//
//}
//
//fun <T> MutableSharedFlow<T>.withSource(
//    scope: CoroutineScope,
//    source: Flow<T>
//): MutableSharedFlow<T> = withSource(scope, source) {
//    emit(it)
//}