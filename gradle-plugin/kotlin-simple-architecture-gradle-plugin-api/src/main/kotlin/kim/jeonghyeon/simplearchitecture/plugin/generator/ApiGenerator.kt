package kim.jeonghyeon.simplearchitecture.plugin.generator

import kim.jeonghyeon.annotation.Api
import kim.jeonghyeon.simplearchitecture.plugin.model.GeneratedApiSource
import kim.jeonghyeon.simplearchitecture.plugin.model.PluginOptions
import kim.jeonghyeon.simplearchitecture.plugin.model.SOURCE_SET_NAME_COMMON
import kim.jeonghyeon.simplearchitecture.plugin.util.*
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtPackageDirective
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType
import java.io.File
// TODO: 29/06/20 if sqldelight model is used. serializable is not supported
//  List, nullable, set also not supported. String.serializer().list/nullable/set
//  if add comment above function. it misdetect suspend not exists.
//  if import list contains the imports which is added here. then duplication error occurs
class ApiGenerator(
    private val pluginOptions: PluginOptions,
    private val origin: Collection<KtFile>
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

    private val KtFile.generatedApiSources
        get(): List<GeneratedApiSource> = getChildrenOfType<KtClass>()
            .filter { it.isApiInterface() }
            .map {
                GeneratedApiSource(
                    getApiImplementationName(it.name!!) + ".kt",
                    it.name!!,
                    packageFqName.asString(),
                    pluginOptions.getGeneratedTargetVariantsPath(),
                    it.makeApiClassSource()
                )
            }

    private fun KtClass.isApiInterface(): Boolean = name != null && isInterface() && hasAnnotation<Api>()


    private fun KtClass.makeApiClassSource(): String = """
    |// $GENERATED_FILE_COMMENT
    |${makePackage()}
    |${makeImport()}
    |
    |${makeClassDefinition()} {
    |
    |$INDENT${makeMainPathProperty()}
    |
    |${makeFunctions().prependIndent(INDENT)}
    |}
    """.trimMargin()

    //todo if package is empty?
    private fun KtClass.makePackage(): String = parent.getChildOfType<KtPackageDirective>()!!.text

    //todo if import is nuLL
    private fun KtClass.makeImport(): String = """
        |${importList.text}
        |import io.ktor.client.HttpClient
        |import io.ktor.client.request.post
        |import io.ktor.client.statement.HttpResponse
        |import io.ktor.client.statement.readText
        |import io.ktor.http.ContentType
        |import io.ktor.http.contentType
        |import kim.jeonghyeon.common.net.throwException
        |import kim.jeonghyeon.common.net.validateResponse
        |import kotlinx.serialization.json.Json
        |import kotlinx.serialization.json.JsonConfiguration
        |import kotlinx.serialization.json.json
        |import kotlinx.serialization.builtins.serializer
        |import kotlinx.serialization.builtins.list
        |import kotlinx.serialization.builtins.nullable
        |import kotlinx.serialization.builtins.set
        """.trimMargin()

    private fun KtClass.makeClassDefinition() =
        "class ${getApiImplementationName(name!!)}(val client: HttpClient, val baseUrl: String) : $name"

    private fun KtClass.makeMainPathProperty(): String =
        "val mainPath = \"${parent.getChildOfType<KtPackageDirective>()!!.fqName.asString()
            .replace(".", "/")}/${name}\""

    private fun KtClass.makeFunctions(): String = functions
        .filter { !it.hasBody() }
        .also { check(it.all { it.isSuspend() }) { "$name has abstract function which is not suspend" } }
        .map { it.makeFunction() }
        .joinToString("\n\n") { it }

    private fun KtNamedFunction.makeFunction(): String = """
    |override ${nameAndPrefix}(${parameters.joinToString { it.nameAndType }})${returnTypeName?.let { ": $it" } ?: ""} {
    |${makeFunctionBody().prependIndent(INDENT)}
    |}
    """.trimMargin()

    private fun KtNamedFunction.makeFunctionBody(): String {
        val returnTypeString = typeReference?.text?.takeIf { it != "Unit" && it != "kotlin.Unit" }
        return """
        |val subPath = "$name"
        |val baseUrlWithoutSlash = if (baseUrl.last() == '/') baseUrl.take(baseUrl.lastIndex) else baseUrl
        |val response = try {
        |${INDENT}client.post<HttpResponse>(baseUrlWithoutSlash + "/" + mainPath + "/" + subPath) {
        |$INDENT${INDENT}contentType(ContentType.Application.Json)
        |$INDENT${INDENT}body = json {
        |${parameters.joinToString("\n") { """"${it.name}" to ${it.name}""" }
            .prependIndent(indent(3))}
        |$INDENT$INDENT}
        |$INDENT}
        |} catch (e: Exception) {
        |${INDENT}client.throwException(e)
        |}
        |
        |client.validateResponse(response)
        |${returnTypeString?.let {
            """
            |val json = Json(JsonConfiguration.Stable)
            |return json.parse(${makeSerializer(it)}, response.readText())
            """.trimMargin()
        } ?: ""}
        """.trimMargin()
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
                |${INDENT}return when (T::class) {
                |${joinToString("\n") { "${it.name}::class -> ${it.name}Impl(this, baseUrl) as T" }.prependIndent(
                        indent(2)
                    )}
                |
                |$INDENT${INDENT}else -> error("can not create " + T::class.simpleName)
                |$INDENT}
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
                val innerType = typeString.substring(typeString.indexOf("<") + 1, typeString.lastIndexOf(">"))
                makeSerializer(innerType) + when (firstType) {
                    "List" -> {
                        ".list"
                    }
                    "Set" -> {
                        ".set"
                    }
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

const val INDENT_LENGTH = 4
const val INDENT = "    "

fun indent(count: Int) = " ".repeat(INDENT_LENGTH * count)