package kim.jeonghyeon.backend.net

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.Headers
import io.ktor.util.pipeline.PipelineContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class PipelineContextStore(val context: PipelineContext<Unit, ApplicationCall>) :
    CoroutineContext.Element {
    override val key: CoroutineContext.Key<*>
        get() = Key

    companion object Key : CoroutineContext.Key<PipelineContextStore>

}

/**
 * use for api controller
 */
suspend fun headers(): Headers =
    coroutineContext[PipelineContextStore]!!.context.call.request.headers