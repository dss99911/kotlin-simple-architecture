package kim.jeonghyeon.net

import io.ktor.client.statement.*
import kim.jeonghyeon.type.AtomicReference
import kim.jeonghyeon.type.atomic
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

/**
 * this is used to catch response of api outside of generated api implementation
 */
class HttpResponseStore : CoroutineContext.Element {
    override val key: CoroutineContext.Key<*>
        get() = Key

    companion object Key : CoroutineContext.Key<HttpResponseStore>

    var response: AtomicReference<HttpResponse?> = atomic(null)
}

suspend fun response(): HttpResponse = coroutineContext[HttpResponseStore]!!.response.value!!

internal suspend fun setResponse(response: HttpResponse) {
    coroutineContext[HttpResponseStore]?.response?.value = response
}

