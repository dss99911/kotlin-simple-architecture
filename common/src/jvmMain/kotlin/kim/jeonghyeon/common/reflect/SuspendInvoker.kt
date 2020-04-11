package kim.jeonghyeon.common.reflect

import java.lang.reflect.Method
import java.lang.reflect.Proxy
import kotlin.coroutines.Continuation

typealias SuspendInvoker = suspend (method: Method, arguments: List<Any?>) -> Any?

private interface SuspendFunction {
    suspend fun invoke(): Any?
}

private val SuspendRemover = SuspendFunction::class.java.methods[0]

@Suppress("UNCHECKED_CAST")
fun <C> suspendProxy(contract: Class<C>, invoker: SuspendInvoker): C =
    Proxy.newProxyInstance(contract.classLoader, arrayOf(contract)) { _, method, arguments ->
        val continuation = arguments.last() as Continuation<*>
        val argumentsWithoutContinuation = arguments.take(arguments.size - 1)
        SuspendRemover.invoke(object : SuspendFunction {
            override suspend fun invoke() = invoker(method, argumentsWithoutContinuation)
        }, continuation)
    } as C