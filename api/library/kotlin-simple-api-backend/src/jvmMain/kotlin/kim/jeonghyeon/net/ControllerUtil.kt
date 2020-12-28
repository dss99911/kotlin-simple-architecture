package kim.jeonghyeon.net

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.sessions.*
import io.ktor.util.*
import kotlin.coroutines.coroutineContext

object ControllerUtil {
    suspend fun call(): ApplicationCall =
        coroutineContext[PipelineContextStore]!!.context.call

    suspend fun headers(): Headers = call().request.headers

    suspend fun setResponded() { coroutineContext[PipelineContextStore]!!.responded = true }

    suspend fun sessions(): CurrentSession = call().sessions

    suspend fun attributes(): Attributes = call().attributes

    suspend fun authentication(): AuthenticationContext = call().authentication

}