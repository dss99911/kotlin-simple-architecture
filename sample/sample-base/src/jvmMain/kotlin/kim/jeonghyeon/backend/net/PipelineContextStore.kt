package kim.jeonghyeon.backend.net

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.AuthenticationContext
import io.ktor.auth.authentication
import io.ktor.http.Headers
import io.ktor.sessions.CurrentSession
import io.ktor.sessions.sessions
import io.ktor.util.pipeline.PipelineContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class PipelineContextStore(val context: PipelineContext<Unit, ApplicationCall>) :
    CoroutineContext.Element {
    override val key: CoroutineContext.Key<*>
        get() = Key

    companion object Key : CoroutineContext.Key<PipelineContextStore>

}

suspend fun call(): ApplicationCall =
    coroutineContext[PipelineContextStore]!!.context.call

suspend fun headers(): Headers = call().request.headers

suspend fun sessions(): CurrentSession = call().sessions

suspend fun authentication(): AuthenticationContext = call().authentication

