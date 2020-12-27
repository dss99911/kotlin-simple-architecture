package kim.jeonghyeon.simplearchitecture.plugin.generator

import kim.jeonghyeon.annotation.*
import kim.jeonghyeon.simplearchitecture.plugin.model.*
import kim.jeonghyeon.simplearchitecture.plugin.util.*
import java.io.File
import kotlin.reflect.KClass

class ApiGenerator(
    private val pluginOptions: PluginOptions,
    private val origin: Collection<SharedKtFile>
) {
    /**
     * this is called two times. so, 2nd time's [origin] contains generated file as well.
     * we ignore if generated file already exists.
     */
    fun generate(): Collection<File> {
        val apiSources = origin
            .flatMap { it.generatedApiSources }

        val apiFiles = apiSources
            .mapNotNull { it.generateApiClassFile() }

        val apiFunctionFiles = apiSources
            .generateApiFunctionFile()

        return apiFiles + apiFunctionFiles
    }

    private val SharedKtFile.generatedApiSources
        get(): List<GeneratedApiSource> = getChildrenOfKtClass()
            .filter { it.isInterface()}
            .filter { it.hasApiAnnotation() || it.hasRetrofitAnnotation() }
            .map {
                GeneratedApiSource(
                    getApiImplementationName(it.name) + ".kt",
                    it.name,
                    packageFqName,
                    pluginOptions.getGeneratedTargetVariantsPath(),
                    it.makeApiClassSource()
                )
            }

    private fun SharedKtClass.hasApiAnnotation(): Boolean = hasAnnotation(Api::class)
    private fun SharedKtClass.hasRetrofitAnnotation(): Boolean = functions.any { it.isRetrofitFunction() }

    private fun SharedKtNamedFunction.isRetrofitFunction(): Boolean {
        return !hasBody() && isSuspend() && requestMethodAnnotationNamesRetrofit.keys.mapNotNull { getAnnotationString(it) }.any()
    }

    private fun SharedKtClass.makeApiClassSource(): String = """
    |// $GENERATED_FILE_COMMENT
    |@file:Suppress("EXPERIMENTAL_API_USAGE")
    |${packageName?.takeIf { it.isNotEmpty() }?.let { "package $it" } ?: ""}
    |${makeImport()}
    |
    |${makeClassDefinition()} {
    |
    |    ${makeMainPathProperty()}
    |
    |${makeFunctions().prependIndent("    ")}
    |}
    """.trimMargin()

    private fun SharedKtClass.makeImport(): String = """
        |${importSourceCode}
        |import io.ktor.client.HttpClient
        |import io.ktor.client.request.*
        |import io.ktor.client.statement.*
        |import kim.jeonghyeon.net.*
        |import kim.jeonghyeon.annotation.*
        |import kotlinx.coroutines.*
        |import io.ktor.http.*
        |import kotlinx.serialization.json.*
        |import kotlinx.serialization.builtins.*
        |import kotlin.coroutines.coroutineContext
        """.trimMargin()

    private fun SharedKtClass.makeClassDefinition() =
        "class ${getApiImplementationName(name)}(val client: HttpClient, val baseUrl: String, val responseTransformer: ResponseTransformer) : $name"

    private fun SharedKtClass.makeMainPathProperty(): String {

        val path = getDefinedPathStatement() ?: "\"${packageName?.replace(".", "/")?.let { "$it/" } ?: ""}${name}\""
        val retrofitPath = getDefinedPathStatement() ?: "\"\""

        return "val mainPath = $path\n" +
                "   val retrofitPath = $retrofitPath"
    }

    private fun SharedKtClass.getDefinedPathStatement(): String? {
        return getAnnotationString(Api::class)
            ?.trimParenthesis()
            ?.getParameterString(Api::path.name, 0)
    }

    private fun SharedKtClass.makeFunctions(): String {
        val functions = functions
            .filter { !it.hasBody() }
            .also { check(it.all { it.isSuspend() }) { "$name has abstract function which is not suspend" } }

        if (functions.map { it.name }.toSet().size < functions.size) {
            error("doesn't support same name on ${this.name}")
        }

        return functions
            .map { it.makeFunction() }
            .joinToString("\n\n") { it }
    }

    private fun SharedKtNamedFunction.makeFunction(): String = """
    |override suspend fun ${name}(${parameters.joinToString { "${it.name}:${it.type}" }})${returnTypeName?.let { ": $it" } ?: ": Unit"} = SimpleApiUtil.run {
    |${makeFunctionBody().prependIndent("    ")}
    |}
    """.trimMargin()

    private fun SharedKtNamedFunction.makeFunctionBody(): String {
        val isAuthenticating = getAnnotationString(Authenticate::class) != null || ktClass?.getAnnotationString(Authenticate::class) != null
        return """
        |val callInfo = ApiCallInfo(baseUrl, ${if (isRetrofitFunction()) "retrofitPath" else "mainPath"}, ${makeSubPathStatement()}, "${(ktClass!!.packageName?.let { "$it." }?:"") + ktClass!!.name}", "$name", HttpMethod.${getRequestMethodFunctionName()}, $isAuthenticating,
        |    listOf(
        |        ${parameters.map { it.toParameterInfo() }.joinToString(",\n        ")}
        |    )
        |)
        |return client.callApi(callInfo, responseTransformer)
        """.trimMargin()
    }

    private fun SharedKtParameter.toParameterInfo(): String = when {
        getAnnotationString(Body::class) != null -> {
            makeApiParameterInfo(ApiParameterType.BODY, "\"${name}\"", null.toString(), null.toString(), "${name}.toJsonElement()")
        }
        getAnnotationString(retrofit2.http.Body::class) != null -> {
            makeApiParameterInfo(ApiParameterType.BODY, "\"${name}\"", null.toString(), null.toString(), "${name}.toJsonElement()")
        }
        getAnnotationString(Query::class) != null -> {
            makeApiParameterInfo(ApiParameterType.QUERY, "\"${name}\"", getParameterKey(Query::class), "${name}.toParameterString()", null.toString())
        }
        getAnnotationString(retrofit2.http.Query::class) != null -> {
            makeApiParameterInfo(ApiParameterType.QUERY, "\"${name}\"", getParameterKeyRetrofit(retrofit2.http.Query::class), "${name}.toParameterString()", null.toString())
        }
        getAnnotationString(Header::class) != null -> {
            makeApiParameterInfo(ApiParameterType.HEADER, "\"${name}\"", getParameterKey(Header::class), "${name}.toParameterString()", null.toString())
        }
        getAnnotationString(retrofit2.http.Header::class) != null -> {
            makeApiParameterInfo(ApiParameterType.HEADER, "\"${name}\"", getParameterKeyRetrofit(retrofit2.http.Header::class), "${name}.toParameterString()", null.toString())
        }
        getAnnotationString(Path::class) != null -> {
            makeApiParameterInfo(ApiParameterType.PATH, "\"${name}\"", getParameterKey(Path::class), "${name}.toParameterString()", null.toString())
        }
        getAnnotationString(retrofit2.http.Path::class) != null -> {
            makeApiParameterInfo(ApiParameterType.PATH, "\"${name}\"", getParameterKeyRetrofit(retrofit2.http.Path::class), "${name}.toParameterString()", null.toString())
        }
        else -> {
            makeApiParameterInfo(ApiParameterType.NONE, "\"${name}\"", null.toString(), null.toString(), "${name}.toJsonElement()")
        }
    }

    private fun SharedKtParameter.getParameterKey(parameterType: KClass<*>): String =
        getAnnotationString(parameterType)!!
            .trimParenthesis()!!
            .getParameterString(Query::name.name, 0)!!//all parametetType's key's field name should be 'name' and index is 0

    private fun SharedKtParameter.getParameterKeyRetrofit(parameterType: KClass<*>): String =
        getAnnotationString(parameterType)!!
            .trimParenthesis()!!
            .getParameterString(retrofit2.http.Query::value.name, 0)!!//all parametetType's key's field name should be 'name' and index is 0

    private fun makeApiParameterInfo(type: ApiParameterType, parameterName: String, key: String, value: String, jsonElement: String): String {
        return """ApiParameterInfo(ApiParameterType.${type.name}, $parameterName, $key, $value, $jsonElement)"""
    }

    private fun SharedKtNamedFunction.makeSubPathStatement(): String {
        return getRequestMethodAnnotationString()
            ?.trimParenthesis()
            ?.let {
                it.getParameterString(Get::path.name, 0)//each method type's 'path' field name should be same
                    ?:it.getParameterString(retrofit2.http.GET::value.name, 0)//for retrofit
            } ?: "\"$name\""
    }

    private fun SharedKtNamedFunction.getRequestMethodFunctionName(): String =
        getRequestMethodAnnotation().simpleName!!

    val requestMethodAnnotationNames = listOf(Get::class, Delete::class, Head::class, Options::class, Patch::class, Put::class, Post::class)
    val requestMethodAnnotationNamesRetrofit = mapOf(
        retrofit2.http.GET::class to Get::class,
        retrofit2.http.DELETE::class to Delete::class,
        retrofit2.http.HEAD::class to Head::class,
        retrofit2.http.OPTIONS::class to Options::class,
        retrofit2.http.PATCH::class to Patch::class,
        retrofit2.http.PUT::class to Put::class,
        retrofit2.http.POST::class to Post::class
    )

    private fun SharedKtNamedFunction.getRequestMethodAnnotationString(): String? {
        return requestMethodAnnotationNames.mapNotNull {
            getAnnotationString(it)
        }.firstOrNull()?:(requestMethodAnnotationNamesRetrofit.keys.mapNotNull { getAnnotationString(it) }.firstOrNull())
    }

    private fun SharedKtNamedFunction.getRequestMethodAnnotation(): KClass<out Annotation> {
        return requestMethodAnnotationNames.firstOrNull {
            getAnnotationString(it) != null
        }?:(requestMethodAnnotationNamesRetrofit.keys.firstOrNull { getAnnotationString(it) != null }?.let { requestMethodAnnotationNamesRetrofit[it] }?: Post::class)
    }

    private fun GeneratedApiSource.generateApiClassFile(): File? =
        File("$sourceSetPath/${packageName.replace(".", "/")}/$fileName")
            .takeIf { !it.exists() }
            ?.write { append(source) }

    private fun List<GeneratedApiSource>.generateApiFunctionFile(): List<File> {
        var expectFile: File? = null
        val filePath = "${pluginOptions.packagePath}/generated/net/HttpClient${pluginOptions.postFix.capitalize()}Ex.kt"
        if (pluginOptions.isMultiplatform) {
            val expectPath = generatedSourceSetPath(pluginOptions.buildPath, SOURCE_SET_NAME_COMMON)
            expectFile = File("$expectPath/$filePath")
                .takeIf { !it.exists() } // e: java.lang.IllegalStateException: Unable to collect additional sources in reasonable number of iterations
                ?.write {
                    append(
                        """
                        // $GENERATED_FILE_COMMENT
                        package ${pluginOptions.packageName}.generated.net

                        import io.ktor.client.HttpClient
                        import kim.jeonghyeon.net.*

                        expect inline fun <reified T> HttpClient.create${pluginOptions.postFix.capitalize()}(baseUrl: String, responseTransformer: ResponseTransformer = ResponseTransformerInternal.getDefaultResponseTransformer()): T

                        """.trimIndent()
                    )
                }
        }

        if (pluginOptions.platformType == KotlinPlatformType.common) {
            return listOfNotNull(expectFile)
        }

        val actualPath = pluginOptions.getGeneratedTargetVariantsPath().let {
            File("$it/$filePath")
                .takeIf { !it.exists() } // e: java.lang.IllegalStateException: Unable to collect additional sources in reasonable number of iterations
                ?.write {
                append(
                    """
                |// $GENERATED_FILE_COMMENT
                |package ${pluginOptions.packageName}.generated.net
                |
                |import io.ktor.client.HttpClient
                |import kim.jeonghyeon.net.*
                |${joinToString("\n") { "import ${if (it.packageName.isEmpty()) "" else "${it.packageName}."}${it.name}" }}
                |${joinToString("\n") { "import ${if (it.packageName.isEmpty()) "" else "${it.packageName}."}${it.name}Impl" }}
                |
                |${if (pluginOptions.isMultiplatform) "actual " else ""}inline fun <reified T> HttpClient.create${pluginOptions.postFix.capitalize()}(baseUrl: String, responseTransformer: ResponseTransformer${if (pluginOptions.isMultiplatform) "" else " = ResponseTransformerInternal.getDefaultResponseTransformer()"}): T {
                |
                |    return when (T::class) {
                |${joinToString("\n") { "${it.name}::class -> ${it.name}Impl(this, baseUrl, responseTransformer) as T" }.prependIndent("        ")}
                |
                |        else -> error("can not create " + T::class.simpleName)
                |    }
                |}
                """.trimMargin()
                )
            }
        }
        return listOfNotNull(expectFile, actualPath)
    }
}

const val GENERATED_FILE_COMMENT = "GENERATED by Simple Api Plugin"

fun getApiImplementationName(interfaceName: String) = interfaceName + "Impl"