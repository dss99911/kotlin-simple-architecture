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
            .filter { it.isApiInterface() }
            .map {
                GeneratedApiSource(
                    getApiImplementationName(it.name) + ".kt",
                    it.name,
                    packageFqName,
                    pluginOptions.getGeneratedTargetVariantsPath(),
                    it.makeApiClassSource()
                )
            }

    private fun SharedKtClass.isApiInterface(): Boolean = isInterface() && hasAnnotation(Api::class)

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
        "class ${getApiImplementationName(name)}(val client: HttpClient, val baseUrl: String) : $name"

    private fun SharedKtClass.makeMainPathProperty(): String {

        val path = getDefinedPathStatement() ?: "\"${packageName?.replace(".", "/")?.let { "$it/" } ?: ""}${name}\""

        return "val mainPath = $path"
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
        |val callInfo = ApiCallInfo(baseUrl, mainPath, ${makeSubPathStatement()}, "${(ktClass!!.packageName?.let { "$it." }?:"") + ktClass!!.name}", "$name", HttpMethod.${getRequestMethodFunctionName()}, $isAuthenticating,
        |    listOf(
        |        ${parameters.map { it.toParameterInfo() }.joinToString(",\n        ")}
        |    )
        |)
        |return client.callApi(callInfo)
        """.trimMargin()
    }

    private fun SharedKtParameter.toParameterInfo(): String = when {
        getAnnotationString(Body::class) != null -> {
            makeApiParameterInfo(ApiParameterType.BODY, "\"${name}\"", null.toString(), null.toString(), "${name}.toJsonElement()")
        }
        getAnnotationString(Query::class) != null -> {
            makeApiParameterInfo(ApiParameterType.QUERY, "\"${name}\"", getParameterKey(Query::class), "${name}.toParameterString()", null.toString())
        }
        getAnnotationString(Header::class) != null -> {
            makeApiParameterInfo(ApiParameterType.HEADER, "\"${name}\"", getParameterKey(Header::class), "${name}.toParameterString()", null.toString())
        }
        getAnnotationString(Path::class) != null -> {
            makeApiParameterInfo(ApiParameterType.PATH, "\"${name}\"", getParameterKey(Path::class), "${name}.toParameterString()", null.toString())
        }
        else -> {
            makeApiParameterInfo(ApiParameterType.NONE, "\"${name}\"", null.toString(), null.toString(), "${name}.toJsonElement()")
        }
    }

    private fun SharedKtParameter.getParameterKey(parameterType: KClass<*>): String =
        getAnnotationString(parameterType)!!
            .trimParenthesis()!!
            .getParameterString(Query::name.name, 0)!!//all parametetType's key's field name should be 'name' and index is 0

    private fun makeApiParameterInfo(type: ApiParameterType, parameterName: String, key: String, value: String, jsonElement: String): String {
        return """ApiParameterInfo(ApiParameterType.${type.name}, $parameterName, $key, $value, $jsonElement)"""
    }

    private fun SharedKtNamedFunction.makeSubPathStatement(): String {
        return getRequestMethodAnnotationString()
            ?.trimParenthesis()
            ?.getParameterString(Get::path.name, 0)//each method type's 'path' field name should be same
            ?: "\"$name\""
    }

    private fun SharedKtNamedFunction.getRequestMethodFunctionName(): String =
        getRequestMethodAnnotation().simpleName!!

    val requestMethodAnnotationNames = listOf(Get::class, Delete::class, Head::class, Options::class, Patch::class, Put::class, Post::class)

    private fun SharedKtNamedFunction.getRequestMethodAnnotationString(): String? {
        return requestMethodAnnotationNames.mapNotNull {
            getAnnotationString(it)
        }.firstOrNull()
    }

    private fun SharedKtNamedFunction.getRequestMethodAnnotation(): KClass<out Annotation> {
        return requestMethodAnnotationNames.firstOrNull {
            getAnnotationString(it) != null
        }?:Post::class
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
                .takeIf { !it.exists() }
                ?.write {
                    append(
                        """
                        // $GENERATED_FILE_COMMENT
                        package ${pluginOptions.packageName}.generated.net

                        import io.ktor.client.HttpClient

                        expect inline fun <reified T> HttpClient.create${pluginOptions.postFix.capitalize()}(baseUrl: String): T

                        """.trimIndent()
                    )
                }
        }

        val actualPath = pluginOptions.getGeneratedTargetVariantsPath().let {
            File("$it/$filePath").takeIf { !it.exists() }?.write {
                append(
                    """
                |// $GENERATED_FILE_COMMENT
                |package ${pluginOptions.packageName}.generated.net
                |
                |import io.ktor.client.HttpClient
                |${joinToString("\n") { "import ${if (it.packageName.isEmpty()) "" else "${it.packageName}."}${it.name}" }}
                |${joinToString("\n") { "import ${if (it.packageName.isEmpty()) "" else "${it.packageName}."}${it.name}Impl" }}
                |
                |${if (pluginOptions.isMultiplatform) "actual " else ""}inline fun <reified T> HttpClient.create${pluginOptions.postFix.capitalize()}(baseUrl: String): T {
                |
                |    return when (T::class) {
                |${joinToString("\n") { "${it.name}::class -> ${it.name}Impl(this, baseUrl) as T" }.prependIndent("        ")}
                |
                |        else -> error("can not create " + T::class.simpleName)
                |    }
                |}
                """.trimMargin()
                )
            }
        }
        return listOfNotNull(actualPath, expectFile)
    }
}

const val GENERATED_FILE_COMMENT = "GENERATED by Simple Api Plugin"

fun getApiImplementationName(interfaceName: String) = interfaceName + "Impl"