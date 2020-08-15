package kim.jeonghyeon.net

import io.ktor.client.statement.HttpResponse
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

/**
 * this is used to catch response of api outside of generated api implementation
 */
class HttpResponseStore : CoroutineContext.Element {
    override val key: CoroutineContext.Key<*>
        get() = Key

    companion object Key : CoroutineContext.Key<HttpResponseStore>

    var response: HttpResponse? = null
}

suspend fun response(): HttpResponse = coroutineContext[HttpResponseStore]!!.response!!

internal suspend fun setResponse(response: HttpResponse) {
    coroutineContext[HttpResponseStore]?.response = response
}

