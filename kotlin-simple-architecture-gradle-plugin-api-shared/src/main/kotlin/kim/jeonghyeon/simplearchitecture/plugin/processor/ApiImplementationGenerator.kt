package kim.jeonghyeon.simplearchitecture.plugin.processor

import kim.jeonghyeon.annotation.Api
import kim.jeonghyeon.simplearchitecture.plugin.model.PluginOptions
import kim.jeonghyeon.simplearchitecture.plugin.model.SOURCE_SET_NAME_COMMON
import kim.jeonghyeon.simplearchitecture.plugin.model.generatedSourceSetPath
import kim.jeonghyeon.simplearchitecture.plugin.util.*
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtPackageDirective
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType
import java.io.File

class ApiImplementationGenerator(
    private val pluginOptions: PluginOptions,
    private val origin: Collection<KtFile>,
    val project: Project
) {

    private val existingFilePackages = origin
        .map { it.packageFqName.asString() + "." + it.name }
        .toSet()

    /**
     * this is called two times. so, 2nd time's [origin] contains generated file as well.
     * we should ignore if generated file already exists.
     * this is not good approach.
     * when we have to delete files before rebuild,
     * if we delete files in this function, KtFile is already created from file. but actual file is deleted.
     * so, file and ktfile is not synced. can cause issues.
     * todo
     *  1. add delete task on compile task
     *  2. move this as well to main gradle module with mock project
     *  the below is how to apply delete task on multiplatform. looks more complicated than current way
     *  tasks.create(TASK_CLEAN, Delete::class.java) {
     *        delete(File(generatedPath))
     *    }
     *   Multiplatform project.
     *    multiplatformExtension?.let { ext ->
     *        ext.targets.forEach { target ->
     *            //KotlinCompilation
     *            // compilationName : debug
     *            // compileKotlinTaskName : compileDebugKotlinAndroid
     *            // defaultSourceSetName : androidDebug
     *            // kotlinSourceSets : [source set androidDebug, source set commonMain]
     *            // platformType : andridJvm
     *            // moduleName : common_debug
     *            // name : debug
     *            target.compilations.forEach {
     *                it.compileKotlinTask.dependsOn(TASK_CLEAN)
     *            }
     *        }
     *        return
     *    }
     */
    fun generateImplementation(): Collection<KtFile> {
        val apiSources = origin
            .flatMap { it.generatedApiSources }
            .filter { !existingFilePackages.contains(it.packageName + "." + it.implFileName) }

        val apiFiles = apiSources
            .map { it.generateApiClassFile() }
            .map { it.toKtFile(project) }

        val apiFunctionFiles = apiSources
            .generateApiFunctionFile()
            .map { it.toKtFile(project) }

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

        println("functions :${makeFunctions()} ${functions.size}, ${functions.map { it.hasBody() }
            .joinToString { it.toString() }}")

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

    private fun GeneratedApiSource.generateApiClassFile(): File =
        File("$sourceSetPath/${packageName.replace(".", "/")}/${implFileName}")
            .write { append(source) }

    private fun List<GeneratedApiSource>.generateApiFunctionFile(): List<File> {
        if (isEmpty()) {
            return emptyList()
        }

        var expectFile: File? = null
        val filePath = "kim/jeonghyeon/generated/net/HttpClientEx.kt"
        if (pluginOptions.hasCommon()) {
            val expectPath = generatedSourceSetPath(pluginOptions.buildPath, SOURCE_SET_NAME_COMMON)
            expectFile = File("$expectPath/$filePath").write {
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
            File("$it/$filePath").write {
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
