package kim.jeonghyeon.net

import com.google.gson.Gson
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.features.ContentTransformationException
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import kim.jeonghyeon.annotation.*
import kim.jeonghyeon.net.error.ApiError
import kim.jeonghyeon.net.error.ApiErrorBody
import kim.jeonghyeon.net.error.DeeplinkError
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.serializer
import java.lang.reflect.InvocationTargetException
import java.util.*
import kotlin.reflect.*
import kotlin.reflect.full.*
import kotlin.reflect.jvm.jvmErasure

private val preControllers: MutableList<Any> = mutableListOf()

fun Application.addControllerBeforeInstallSimpleRouting(_controller: Any) {
    check(featureOrNull(SimpleRouting) == null) {
        "SimpleRouting should be installed after adding '${_controller::class.simpleName}'"
    }

    preControllers.add(_controller)
}

/**
 * TODO provide customization option for
 *  - StatusPages
 */
class SimpleRouting(val config: Configuration) {
    class Configuration {
        @SimpleArchInternal
        val controllerList = mutableListOf(*preControllers.toTypedArray())
        var configure: (Routing.() -> Unit)? = null

        @OptIn(SimpleArchInternal::class)
        operator fun Any.unaryPlus() {
            controllerList.add(this)
        }

        @SimpleArchInternal
        var hasSignFeature = false//todo consider other way if there is additional customization required

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

    @OptIn(SimpleArchInternal::class)
    fun initialize(pipeline: Application) {
        pipeline.install(StatusPages) {
            val unknownException: suspend PipelineContext<Unit, ApplicationCall>.(exception: Throwable) -> Unit =
                {
                    it.printStackTrace()
                    println("[SimpleRouting] ${it.message}")
                    call.respond(
                        HttpStatusCode.ApiError,
                        ApiErrorBody(ApiErrorBody.CODE_UNKNOWN, it.message)
                    )
                }

            val apiException: suspend PipelineContext<Unit, ApplicationCall>.(exception: ApiError) -> Unit =
                {
                    it.printStackTrace()
                    println("[SimpleRouting] ${it.message}")
                    call.respond(HttpStatusCode.ApiError, it.body)
                }

            val deeplinkException: suspend PipelineContext<Unit, ApplicationCall>.(exception: DeeplinkError) -> Unit =
                {
                    it.printStackTrace()
                    println("[SimpleRouting] ${it.message}")
                    call.respond(HttpStatusCode.DeeplinkError, it.deeplinkInfo)
                }

            exception<Throwable> { error ->
                unknownException(error)
            }

            exception<ApiError> { error ->
                apiException(error)
            }

            exception<DeeplinkError> { error ->
                deeplinkException(error)
            }

            exception<InvocationTargetException> { error ->
                when (val targetException = error.targetException) {
                    is ApiError -> apiException(targetException)
                    is DeeplinkError -> deeplinkException(targetException)
                    else -> unknownException(targetException)
                }
            }

        }

        pipeline.install(Routing) {
            installControllers(config.controllerList)
            config.configure?.invoke(this)
        }



    }

    private fun Routing.installControllers(controllers: List<Any>) {
        println("[SimpleRouting] Start to install Routing")
        controllers.forEach { installController(it) }
    }

    private fun Routing.installController(controller: Any) {

        controller::class.allSuperclasses
            .filter { it.getApiAnnotation() != null }
            .forEach { apiInterface ->
                val mainPath = apiInterface.getMainPath() ?: return@forEach
                installAuthenticate(apiInterface.annotations) {
                    println("[SimpleRouting] Main Path : $mainPath, ${controller::class.simpleName}")
                    route(mainPath) {
                        this.installSubPaths(controller, apiInterface)
                    }
                }
            }
    }

    @OptIn(SimpleArchInternal::class)
    private fun Route.installAuthenticate(annotations: List<Annotation>, build: Route.() -> Unit) {
        if (!config.hasSignFeature) {
            //ApiBindingController is added as default
            return build()
        }
        val authenticateAnnotation =
            annotations.filterIsInstance<Authenticate>().firstOrNull() ?: return build()
        if (authenticateAnnotation.name.isBlank()) {
            authenticate(build = build)
        } else {
            authenticate(authenticateAnnotation.name, build = build)
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

    private fun Route.installSubPaths(controller: Any, apiInterface: KClass<*>) {
        apiInterface.declaredFunctions.forEach { kfunction ->
            val subPath = kfunction.getSubPath() ?: return@forEach
            val method = kfunction.getMethod()

            installAuthenticate(kfunction.annotations) {
                route(subPath, method) {
                    println("[SimpleRouting]     Sub Path : $subPath $method")
                    handle {
                        handleRequest(controller, kfunction)
                    }
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

    @OptIn(InternalSerializationApi::class, ExperimentalStdlibApi::class)
    private suspend fun PipelineContext<Unit, ApplicationCall>.handleRequest(
        controller: Any,
        apiFunction: KFunction<*>
    ) {
        val controllerFunction = controller.findFunction(apiFunction)

        val body: Any? = getBody()
        val args = apiFunction.parameters
            .subList(1, apiFunction.parameters.size)//first parameter is for suspend function
            .map { param ->
                getArgument(param, body)
            }.toTypedArray()

        val pipelineContextStore = PipelineContextStore(this)
        launch(coroutineContext + pipelineContextStore) {
            val response = controllerFunction.callSuspend(controller, *args)
            if (!pipelineContextStore.responded) {
                call.respond(if (isSerializationConverter()) {
                    Json{}.encodeToJsonElement(serializer(apiFunction.returnType), response)
                } else {
                    Gson().toJson(response, apiFunction.returnType.javaType)
                })
            }
        }.join()
    }

    @OptIn(InternalSerializationApi::class, ExperimentalStdlibApi::class)
    private fun PipelineContext<Unit, ApplicationCall>.getArgument(
        param: KParameter,
        body: Any?
    ): Any? {
        //If use serialization. use serialization
        //if not use serialization, just use gson, because ContentConverter is difficult to use for converting string to type
        //the purpose is that if use serialization, model require Serializable annotation. other converter doesn't require it.
        val json = Json { }
        val gson = Gson()

        fun Any.getValue(key: String): Any? {
            return if (this is Map<*, *>) {
                this[key]
            } else if (this is JsonObject) {
                this[key]
            } else error("${this::class.simpleName} is not supported for body")
        }

        fun Any.toType(type: KType): Any? {
            return if (this is JsonElement) {
                json.decodeFromJsonElement(serializer(type), this)
            } else {
                gson.fromJson(gson.toJson(this), type.javaType)
            }
        }

        fun String.fromStringToType(type: KType): Any? {
            return if (isSerializationConverter()) {
                json.decodeFromString(serializer(type), this)
            } else {
                gson.fromJson(this, type.javaType)
            }
        }

        try {
            val parameterAnnotation = param.annotations.firstOrNull { it.isParameterAnnotation() }
                ?: //if no annotation, it's body argument
                return body!!.getValue(param.name!!)?.let {
                    it.toType(param.type)
                }

            return if (parameterAnnotation is Body) {
                body?.toType(param.type)
            } else {
                when (parameterAnnotation) {
                    is Header -> call.request.headers[parameterAnnotation.name]
                    is Path -> call.parameters[parameterAnnotation.name]
                    is Query -> call.request.queryParameters[parameterAnnotation.name]
                    else -> null
                }?.let {
                    if (param.type.jvmErasure.let { it == String::class || it.java.isEnum }) {
                        //TEXT => "TEXT" => String or enum
                        json.encodeToJsonElement(String.serializer(), it).let {
                            it.toType(param.type)
                        }
                    } else {
                        it.fromStringToType(param.type)
                    }
                }
            }
        } catch (e: Exception) {
            println("[SimpleRouting]" + e.message + "\nparam : ${param.name}")
            throw e
        }
    }

    private fun Annotation.isParameterAnnotation(): Boolean =
        this is Header || this is Path || this is Query || this is Body

    private suspend fun PipelineContext<Unit, ApplicationCall>.getBody(): Any? {
        //todo if body not exists, ignore it with better approach.
        return try {
            //check if it's serialization or not. because, serialization require annotation on model
            if (isSerializationConverter()) {
                call.receive<JsonObject>()
            } else {
                call.receive<Map<String, Any?>>()
            }
        } catch (e: ContentTransformationException) {
            e.printStackTrace()
            null
        }
    }

    private fun PipelineContext<Unit, ApplicationCall>.isSerializationConverter(): Boolean {
        return application.feature(ContentNegotiation)
            .registrations
            .filter { it.contentType == ContentType.Application.Json}
            .filter { it.converter is SerializationConverter }
            .any()
    }

    private fun Any.findFunction(func: KFunction<*>): KFunction<*> =
        this::class.functions.first {
            func.name == it.name &&
                    func.parameters.mapIndexedNotNull { i, v -> if (i == 0) null else v.type } ==
                    it.parameters.mapIndexedNotNull { i, v -> if (i == 0) null else v.type }
        }
}