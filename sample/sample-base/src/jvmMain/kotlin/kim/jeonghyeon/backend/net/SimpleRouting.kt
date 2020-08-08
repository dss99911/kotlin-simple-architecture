package kim.jeonghyeon.backend.net

import io.ktor.application.*
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.ContentTransformationException
import io.ktor.features.StatusPages
import io.ktor.gson.gson
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.util.AttributeKey
import io.ktor.util.error
import io.ktor.util.pipeline.PipelineContext
import kim.jeonghyeon.annotation.*
import kim.jeonghyeon.jvm.extension.toJsonObject
import kim.jeonghyeon.jvm.extension.toJsonString
import kim.jeonghyeon.net.error.ApiError
import kim.jeonghyeon.net.error.ApiErrorBody
import kim.jeonghyeon.net.isUri
import kotlinx.coroutines.launch
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.superclasses
import kotlin.reflect.jvm.javaType

//todo move this to jvm library
//todo support other platfrom by @Api annotation.
class SimpleRouting(val config: Configuration) {
    class Configuration {
        val controllerList = mutableListOf<Any>()
        var logging: Boolean = false

        operator fun Any.unaryPlus() {
            controllerList.add(this)
        }
    }

    companion object Feature : ApplicationFeature<Application, Configuration, SimpleRouting> {
        override val key: AttributeKey<SimpleRouting> = AttributeKey("Simple Api")

        override fun install(
            pipeline: Application,
            configure: Configuration.() -> Unit
        ): SimpleRouting {
            val config = Configuration().apply(configure)
            return SimpleRouting(config).apply { initialize(pipeline) }
        }
    }

    fun initialize(pipeline: Application) {
        pipeline.install(StatusPages) {
            exception<ApiError> { error ->
                call.respond(HttpStatusCode.ApiError, error.body)
            }
            exception<Throwable> { cause ->
                pipeline.environment.log.error(cause)
                call.respond(
                    HttpStatusCode.ApiError,
                    ApiErrorBody(ApiErrorBody.CODE_UNKNOWN, cause.message)
                )
            }
        }

        pipeline.install(ContentNegotiation) {
            gson()
        }

        pipeline.install(Routing) {
            attachRoutes(config.controllerList)
        }

        if (config.logging) {
            pipeline.install(CallLogging)
        }

    }

    private fun Routing.attachRoutes(controllers: List<Any>) {
        controllers.forEach { attachRoute(it) }
    }

    private fun Routing.attachRoute(controller: Any) {

        controller::class.superclasses
            .filter { it.getApiAnnotation() != null }
            .forEach { apiInterface ->
                val mainPath = apiInterface.getMainPath() ?: return@forEach
                route(mainPath) {
                    this.attachSubPaths(controller, apiInterface)
                }
            }
    }

    private fun KClass<*>.getApiAnnotation(): Api? =
        annotations.firstOrNull { annotation -> annotation is Api } as? Api?

    private fun KClass<*>.getMainPath(): String? {
        //todo issue : can't differetiate empty path(root path only) and null path(package name + interface name).
        // so currently doesn't support empty path
        val path = getApiAnnotation()!!.path
        return when {
            path.isEmpty() -> java.name.replace(".", "/")
            path.isUri() -> null //ignore https:// path.
            else -> path
        }
    }

    private fun Route.attachSubPaths(controller: Any, apiInterface: KClass<*>) {
        apiInterface.declaredFunctions.forEach { kfunction ->
            val subPath = kfunction.getSubPath() ?: return@forEach
            val method = kfunction.getMethod()
            route(subPath, method) {
                handle {
                    handleRequest(controller, kfunction)
                }
            }
        }
    }

    private fun KFunction<*>.getSubPath(): String? {
        //todo issue : can't differetiate empty path(root path only) and null path(package name + interface name).
        // so currently doesn't support empty path
        val path = annotations.mapNotNull {
            it.getApiMethodPath()
        }.firstOrNull()

        return when {
            path.isNullOrEmpty() -> name
            path.isUri() -> null //ignore https:// path.
            else -> path
        }
    }

    private fun Annotation.getApiMethodPath(): String? = when (this) {
        is Get -> path
        is Post -> path
        is Put -> path
        is Delete -> path
        is Options -> path
        is Patch -> path
        else -> null
    }

    private fun KFunction<*>.getMethod(): HttpMethod {
        return annotations
            .filter { it.getApiMethodPath() != null }
            .mapNotNull {
                val methodName = it.annotationClass.simpleName?.toUpperCase(Locale.ENGLISH)
                    ?: return@mapNotNull null
                HttpMethod.parse(methodName)
            }.firstOrNull() ?: HttpMethod.Post
    }

    private suspend fun PipelineContext<Unit, ApplicationCall>.handleRequest(
        controller: Any,
        apiFunction: KFunction<*>
    ) {
        val controllerFunction = controller.findFunction(apiFunction)

        val body: Any? = getBody(apiFunction)
        val args = apiFunction.parameters
            .subList(1, apiFunction.parameters.size)//first parameter is for suspend function
            .map { param ->
                getArgument(param, body)
            }.toTypedArray()

        launch(coroutineContext + PipelineContextStore(this)) {
            val response = controllerFunction.callSuspend(controller, *args)
            call.respond(convertResponse(response))
        }
    }

    private fun PipelineContext<Unit, ApplicationCall>.getArgument(param: KParameter, body: Any?): Any? {
        val parameterAnnotation = param.annotations.filter { it.isParameterAnnotation() }.firstOrNull()
        if (parameterAnnotation == null) {
            //if no annotation, it should be body argument
            body as Map<String, Any?>
            return body[param.name].toJsonString()?.toJsonObject<Any>(param.type.javaType)
        }

        return when (parameterAnnotation) {
            is Header -> call.request.headers[parameterAnnotation.name]
            is Path -> call.parameters[parameterAnnotation.name]
            is Query -> call.request.queryParameters[parameterAnnotation.name]
            is Body -> body
            else -> null
        }
    }

    private fun Annotation.isParameterAnnotation(): Boolean = this is Header || this is Path || this is Query || this is Body

    private fun KFunction<*>.getBodyType(): KType? = parameters.firstOrNull { it.annotations.any { it is Body } }?.type
    private suspend fun PipelineContext<Unit, ApplicationCall>.getBody(function: KFunction<*>): Any? {
        val bodyType = function.getBodyType()

        //todo if body not exists, ignore it with better approach.
        return try {
            if (bodyType == null) {
                call.receive<Map<String, Any?>>()
            } else {
                call.receive(bodyType) as Any?//even if return type is Any? if doesn't mention, it convert to Map
            }
        } catch (e: ContentTransformationException) {
            e.printStackTrace()
            null
        }
    }


    private fun convertResponse(response: Any?): Any = if (response is String) {
        //todo is this proper process? how to make json writer to set quotes?
        // if response type is String, then client error with the log below
        // Expected string literal with quotes. Use 'JsonConfiguration.isLenient = true' to accept non-compliant
        "\"${response.replace("\\", "\\\\")}\""
    } else response?:"null" //todo does json writer not know null to null text??

    private fun Any.findFunction(func: KFunction<*>): KFunction<*> =
        this::class.declaredFunctions.first {
            func.name == it.name
        }

}