package kim.jeonghyeon.backend.net

import com.google.gson.Gson
import io.ktor.application.*
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.Routing
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.util.AttributeKey
import io.ktor.util.error
import io.ktor.util.pipeline.PipelineContext
import kim.jeonghyeon.common.net.error.ApiError
import kim.jeonghyeon.common.net.error.ApiErrorBody
import kim.jeonghyeon.common.net.error.ApiErrorCode
import java.lang.reflect.Type
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.superclasses
import kotlin.reflect.jvm.javaType

class SimpleRouting(val config: Configuration) {
    class Configuration {
        val list = mutableListOf<Any>()
        var logging: Boolean = false

        operator fun Any.unaryPlus() {
            list.add(this)
        }
    }

    companion object Feature : ApplicationFeature<Application, Configuration, SimpleRouting> {
        override val key: AttributeKey<SimpleRouting> = AttributeKey("Simple Api")

        override fun install(
            pipeline: Application,
            configure: Configuration.() -> Unit
        ): SimpleRouting {
            val config = Configuration().apply(configure)
            return SimpleRouting(config).apply { attach(pipeline) }
        }
    }

    fun attach(pipeline: Application) {
        pipeline.install(StatusPages) {
            exception<ApiError> { error ->
                call.respond(HttpStatusCode.ApiError, error.body)
            }
            exception<Throwable> { cause ->
                pipeline.environment.log.error(cause)
                call.respond(
                    HttpStatusCode.ApiError,
                    ApiErrorBody(ApiErrorCode.UNKNOWN, cause.message)
                )
            }
        }

        pipeline.install(ContentNegotiation) {
            gson()
        }

        pipeline.install(Routing) {
            attachRoutes(config.list)
        }

        if (config.logging) {
            pipeline.install(CallLogging)
        }

    }

    private fun Routing.attachRoutes(apis: List<Any>) {
        apis.forEach { attach(it) }
    }

    private fun Routing.attach(api: Any) {
        val superclasses = api::class.superclasses.filter {
            it != Any::class//superclass always contains Any::class
        }

        superclasses.forEach { kclass ->
            val mainPath = kclass.java.name.replace(".", "-")
            route(mainPath) {
                this.attachSubPaths(api, kclass)
            }
        }
    }

    private fun Route.attachSubPaths(api: Any, kclass: KClass<*>) {
        kclass.declaredFunctions.forEach { kfunction ->
            val subPath = kfunction.name
            post<List<Any?>>(subPath) { arguments ->
                handleRequest(api, kfunction, arguments)
            }
        }
    }

    private suspend fun PipelineContext<Unit, ApplicationCall>.handleRequest(
        api: Any,
        kfunction: KFunction<*>,
        arguments: List<Any?>
    ) {
        val convertedArgs = arguments.convertType(kfunction).toTypedArray()
        val response = api.findFunction(kfunction).callSuspend(api, *convertedArgs)
        call.respond(response ?: "null")
    }

    private fun Any.findFunction(func: KFunction<*>): KFunction<*> =
        this::class.declaredFunctions.first {
            func.name == it.name
        }

    private fun List<Any?>.convertType(kfunction: KFunction<*>): List<Any?> =
        mapIndexed { index, obj ->
            obj?.toJsonString()?.toJsonObject<Any>(kfunction.parameters[index + 1].type.javaType)
        }

    //TODO HYUN [multi-platform2] : add to kotlin library
    fun <T> String.toJsonObject(type: Type): T = Gson().fromJson(this, type)
    fun Any.toJsonString(): String = Gson().toJson(this)
}