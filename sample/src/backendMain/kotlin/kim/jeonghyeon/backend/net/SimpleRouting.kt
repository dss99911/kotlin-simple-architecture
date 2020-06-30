package kim.jeonghyeon.backend.net

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
import kim.jeonghyeon.jvm.extension.toJsonObject
import kim.jeonghyeon.jvm.extension.toJsonString
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.superclasses
import kotlin.reflect.jvm.javaType

//todo move this to jvm library
//todo support other platfrom by @Api annotation.
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
            val mainPath = kclass.java.name
                .replace(".", "/")

            route(mainPath) {
                this.attachSubPaths(api, kclass)
            }
        }
    }

    private fun Route.attachSubPaths(api: Any, kclass: KClass<*>) {
        kclass.declaredFunctions.forEach { kfunction ->
            val subPath = kfunction.name
            post<Map<String, Any?>>(subPath) { arguments ->
                handleRequest(api, kfunction, arguments)
            }
        }
    }

    private suspend fun PipelineContext<Unit, ApplicationCall>.handleRequest(
        api: Any,
        kfunction: KFunction<*>,
        arguments: Map<String, Any?>
    ) {
        val function = api.findFunction(kfunction)

        val convertedArgs = function.parameters
            .subList(1, function.parameters.size)//first parameter is for suspend function
            .map { param ->
                arguments[param.name].toJsonString()?.toJsonObject<Any>(param.type.javaType)
            }.toTypedArray()

        var response = function.callSuspend(api, *convertedArgs)
        if (response is String) {
            //todo is this proper process? how to make json writer to set quotes?
            // if response type is String, then client error with the log below
            // Expected string literal with quotes. Use 'JsonConfiguration.isLenient = true' to accept non-compliant
            response = "\"$response\""
        }
        //todo does json writer not know null to null text??
        call.respond(response ?: "null")
    }

    private fun Any.findFunction(func: KFunction<*>): KFunction<*> =
        this::class.declaredFunctions.first {
            func.name == it.name
        }

}