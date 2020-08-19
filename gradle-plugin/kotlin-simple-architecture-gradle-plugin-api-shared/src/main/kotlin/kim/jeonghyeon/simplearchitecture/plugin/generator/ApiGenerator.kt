package kim.jeonghyeon.simplearchitecture.plugin.generator

import kim.jeonghyeon.annotation.*
import kim.jeonghyeon.simplearchitecture.plugin.model.*
import kim.jeonghyeon.simplearchitecture.plugin.util.generatedSourceSetPath
import kim.jeonghyeon.simplearchitecture.plugin.util.write
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

    private fun SharedKtClass.isApiInterface(): Boolean = name != null && isInterface() && hasAnnotation(Api::class)

    private fun SharedKtClass.makeApiClassSource(): String = """
    |// $GENERATED_FILE_COMMENT
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

    private fun SharedKtClass.makeFunctions(): String = functions
        .filter { !it.hasBody() }
        .also { check(it.all { it.isSuspend() }) { "$name has abstract function which is not suspend" } }
        .map { it.makeFunction() }
        .joinToString("\n\n") { it }

    private fun SharedKtNamedFunction.makeFunction(): String = """
    |override suspend fun ${name}(${parameters.joinToString { "${it.name}:${it.type}" }})${returnTypeName?.let { ": $it" } ?: ""} {
    |${makeFunctionBody().prependIndent("    ")}
    |}
    """.trimMargin()

    private fun SharedKtNamedFunction.makeFunctionBody(): String {
        val returnTypeString = returnTypeName?.takeIf { it != "Unit" && it != "kotlin.Unit" }

        val isAuthenticating = getAnnotationString(Authenticate::class) != null || ktClass?.getAnnotationString(Authenticate::class) != null
        return """
        |val subPath = ${makeSubPathStatement()}
        |val responseText = client.fetchResponseText($isAuthenticating) { adder ->
        |    client.${getRequestMethodFunctionName()}<HttpResponse>(baseUrl connectPath mainPath connectPath subPath) {
        |        ${makeBodyStatement()}
        |        ${makeContentTypeStatement()}
        |        ${makeQueryStatement()}
        |        ${makeHeaderStatement()}
        |        adder()
        |    }
        |}
        |${returnTypeString?.let { """
        |val json = Json(JsonConfiguration.Stable.copy(ignoreUnknownKeys = true))
        |return json.parse(${makeSerializer(it)}, responseText)
        """.trimMargin() } ?: ""}
        """.trimMargin()
    }

    private fun SharedKtNamedFunction.makeBodyStatement(): String {
        val bodyParameter = parameters.firstOrNull { it.getAnnotationString(Body::class) != null }

        if (bodyParameter != null) {
            return "body = ${bodyParameter.name!!}"
        }

        val bodyParameters = parameters.filter {
            it.getAnnotationString(Header::class) == null
                    && it.getAnnotationString(Path::class) == null
                    && it.getAnnotationString(Query::class) == null
        }

        if (bodyParameters.isEmpty()) {
            return ""
        }

        return """body = json { ${bodyParameters.joinToString("; ") { """"${it.name}" to ${it.name}""" }} }"""
    }

    private fun SharedKtNamedFunction.makeContentTypeStatement(): String = when(getRequestMethodAnnotation()) {
        Post::class, Put::class, Delete::class, Patch::class ->
            "contentType(ContentType.Application.Json)"
        else -> ""
    }

    private fun SharedKtNamedFunction.makeQueryStatement(): String = parameters
        .filter { it.getAnnotationString(Query::class) != null }
        .map {
            val key = it.getAnnotationString(Query::class)!!
                .trimParenthesis()!!
                .getParameterString(Query::name.name, 0)

            "parameter($key, ${it.name})"
        }.joinToString("; ")

    private fun SharedKtNamedFunction.makeHeaderStatement(): String = parameters
        .filter { it.getAnnotationString(Header::class) != null }
        .map {
            val key = it.getAnnotationString(Header::class)!!
                .trimParenthesis()!!
                .getParameterString(Header::name.name, 0)
            "header($key, ${it.name})"
        }.joinToString("; ")

    private fun SharedKtNamedFunction.makeSubPathStatement(): String {
        val subPath = getRequestMethodAnnotationString()
            ?.trimParenthesis()
            ?.getParameterString(Get::path.name, 0)
            ?: "\"$name\""

        return subPath + parameters
            .filter { it.getAnnotationString(Path::class) != null }
            .joinToString("") {
                val pathName = it.getAnnotationString(Path::class)!!
                    .trimParenthesis()!!
                    .getParameterString(Path::name.name, 0)
                """.replace("{" + $pathName + "}", ${it.name})"""
            }

    }

    private fun SharedKtNamedFunction.getRequestMethodFunctionName(): String =
        getRequestMethodAnnotation().simpleName!!.toLowerCase()

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

    private fun makeSerializer(typeString: String): String {
        return when {
            typeString.endsWith("?") -> {
                makeSerializer(typeString.substring(0, typeString.length - 1)) + ".nullable"
            }
            typeString.endsWith(">") -> {
                val firstType = typeString.substringBefore("<")
                val innerTypes = typeString.substring(typeString.indexOf("<") + 1, typeString.lastIndexOf(">")).split(",").map { it.trim() }
                val innerSerializers = innerTypes.map { makeSerializer(it) }

                //todo support with packagename as well, and super type of map
                when (firstType) {
                    "List" -> {
                        "${innerSerializers.first()}.list"
                    }
                    "Set" -> {
                        "${innerSerializers.first()}.set"
                    }
                    "Pair" -> "PairSerializer(${innerSerializers[0]}, ${innerSerializers[1]})"
                    "Map.Entry" -> "MapEntrySerializer(${innerSerializers[0]}, ${innerSerializers[1]})"
                    "Triple" -> "TripleSerializer(${innerSerializers[0]}, ${innerSerializers[1]})"
                    "Map" -> "MapSerializer(${innerSerializers[0]}, ${innerSerializers[1]})"
                    else -> error("Serializing $firstType is not supported by api generator")
                }
            }
            else -> {
                "$typeString.serializer()"
            }
        }
    }
}

const val GENERATED_FILE_COMMENT = "GENERATED by Simple Api Plugin"

fun getApiImplementationName(interfaceName: String) = interfaceName + "Impl"