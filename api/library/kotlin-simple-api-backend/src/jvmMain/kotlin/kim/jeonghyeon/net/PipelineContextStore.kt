package kim.jeonghyeon.net

import io.ktor.application.*
import io.ktor.util.pipeline.*
import kotlin.coroutines.CoroutineContext

class PipelineContextStore(
    val context: PipelineContext<Unit, ApplicationCall>,
    var responded: Boolean = false
) :
    CoroutineContext.Element {
    override val key: CoroutineContext.Key<*>
        get() = Key

    companion object Key : CoroutineContext.Key<PipelineContextStore>

}

