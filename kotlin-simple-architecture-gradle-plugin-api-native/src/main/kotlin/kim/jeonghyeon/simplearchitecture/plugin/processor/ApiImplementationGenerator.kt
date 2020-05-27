package kim.jeonghyeon.simplearchitecture.plugin.processor

import kim.jeonghyeon.annotation.Api
import kim.jeonghyeon.simplearchitecture.plugin.model.PluginOptions
import kim.jeonghyeon.simplearchitecture.plugin.model.SOURCE_SET_NAME_COMMON
import kim.jeonghyeon.simplearchitecture.plugin.model.generatedSourceSetPath
import kim.jeonghyeon.simplearchitecture.plugin.util.*
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtPackageDirective
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType
import java.io.File

/**
 * compiler-embeddable and compiler kotlin library use different Psi related classes.
 * so, it's difficult to use common class for Jvm, Native as this class depends on the Psi related classes a lot.
 * It's required to update this class when Jvm's same class is changed.
 * TODO To make one class.
 *  There is solution by Intellij Platform SDK
 *  But, in order to make PsiFile, need to configure mock project(kotlin compilation, source path setting etc..) of the SDK is required.
 *  I couldn't find the sample to make the mock with Kotlin language.
 *  SqlDelight project use mock. but it's for sql language. and I don't know how to configure for Kotlin
 *  Let's try this later
 *  - Merits by this approach
 *      1. one plugin module is enough. current there are 3 modules for just a simple plugin
 *      2. remove duplication of ApiImplementationGenerator
 *      3. this generating file task should processed before compile task. but in current approach, the task is processed inside of compile task. it's not good structure.
 *      4. if using kapt, this is called two times, able to prevent duplicated process.
 */
class ApiImplementationGenerator(
    private val pluginOptions: PluginOptions,
    private val origin: Collection<KtFile>
) {

    private val existingFilePackages = origin
        .map { it.packageFqName.asString() + "." + it.name }
        .toSet()

    /**
     * this is called two times. so, 2nd time's [origin] contains generated file as well.
     * we ignore if generated file already exists.
     */
    fun generateImplementation(): Collection<File> {
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
            .mapNotNull {
                GeneratedApiSource(
                    name,
                    it.name ?: return@mapNotNull null,
                    packageFqName.asString(),
                    pluginOptions.getGeneratedTargetVariantsPath(),
                    it.makeApiClassSource() ?: return@mapNotNull null
                )
            }


    private fun KtClass.makeApiClassSource(): String? {
        if (!isInterface()) {
            return null
        }

        if (!hasAnnotation<Api>()) {
            return null
        }

        return """
        |// $GENERATED_FILE_COMMENT
        |${makePackage()}
        |${makeImport()}
        |
        |${makeClassDefinition()} {
        |
        |${INDENT}${makeMainPathProperty()}
        |
        |${makeFunctions().prependIndent(INDENT)}
        |}
        """.trimMargin()
    }

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
        |${INDENT}${INDENT}contentType(ContentType.Application.Json)
        |${INDENT}${INDENT}body = json {
        |${parameters.joinToString("\n") { """"${it.name}" to ${it.name}""" }
            .prependIndent(indent(3))}
        |${INDENT}${INDENT}}
        |${INDENT}}
        |} catch (e: Exception) {
        |${INDENT}client.throwException(e)
        |}
        |
        |client.validateResponse(response)
        |${returnTypeString?.let {
            """
            |val json = Json(JsonConfiguration.Stable)
            |return json.parse($it.serializer(), response.readText())
            """.trimMargin()
        } ?: ""}
        """.trimMargin()
    }

    private fun GeneratedApiSource.generateApiClassFile(): File? =
        File("$sourceSetPath/${packageName.replace(".", "/")}/${implFileName}")
            .takeIf { !it.exists() }
            ?.write { append(source) }

    private fun List<GeneratedApiSource>.generateApiFunctionFile(): List<File> {
        var expectFile: File? = null
        val filePath = "kim/jeonghyeon/generated/net/HttpClientEx.kt"
        if (pluginOptions.hasCommon()) {
            val expectPath = generatedSourceSetPath(pluginOptions.buildPath, SOURCE_SET_NAME_COMMON)
            expectFile = File("$expectPath/$filePath")
                .takeIf { !it.exists() }
                ?.write {
                    append(
                        """
                        // $GENERATED_FILE_COMMENT
                        package kim.jeonghyeon.generated.net

                        import io.ktor.client.HttpClient

                        expect inline fun <reified T> HttpClient.create(baseUrl: String): T

                        """.trimIndent()
                    )
                }
        }

        val actualPath = pluginOptions.getGeneratedTargetVariantsPath().let {
            File("$it/$filePath").takeIf { !it.exists() }?.write {
                append(
                    """
                |// $GENERATED_FILE_COMMENT
                |package kim.jeonghyeon.generated.net
                |
                |import io.ktor.client.HttpClient
                |${joinToString("\n") { "import ${if (it.packageName.isEmpty()) "" else "${it.packageName}."}${it.name}" }}
                |${joinToString("\n") { "import ${if (it.packageName.isEmpty()) "" else "${it.packageName}."}${it.name}Impl" }}
                |
                |${if (pluginOptions.hasCommon()) "actual " else ""}inline fun <reified T> HttpClient.create(baseUrl: String): T {
                |
                |${INDENT}return when (T::class) {
                |${joinToString("\n") { "${it.name}::class -> ${it.name}Impl(this, baseUrl) as T" }.prependIndent(
                        indent(2)
                    )}
                |
                |${INDENT}${INDENT}else -> error("can not create " + T::class.qualifiedName)
                |${INDENT}}
                |}
                """.trimMargin()
                )
            }
        }
        return listOf(actualPath, expectFile).filterNotNull()
    }
}

const val GENERATED_FILE_COMMENT = "GENERATED by Simple Api Plugin"

fun getApiImplementationName(interfaceName: String) = interfaceName + "Impl"

const val INDENT_LENGTH = 4
const val INDENT = "    "

fun indent(count: Int) = " ".repeat(INDENT_LENGTH * count)

val GeneratedApiSource.implFileName: String get() = getApiImplementationName(name) + ".kt"
