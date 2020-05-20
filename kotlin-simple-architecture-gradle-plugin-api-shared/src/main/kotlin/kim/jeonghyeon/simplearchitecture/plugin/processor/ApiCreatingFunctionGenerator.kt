package kim.jeonghyeon.simplearchitecture.plugin.processor

import com.squareup.kotlinpoet.*
import kim.jeonghyeon.simplearchitecture.plugin.model.PluginOptions
import kim.jeonghyeon.simplearchitecture.plugin.model.SOURCE_SET_NAME_COMMON
import kim.jeonghyeon.simplearchitecture.plugin.model.generatedSourceSetPath
import kim.jeonghyeon.simplearchitecture.plugin.util.API_CREATION_FUNCTION_FILE_NAME
import kim.jeonghyeon.simplearchitecture.plugin.util.API_CREATION_FUNCTION_PACKAGE_NAME
import kim.jeonghyeon.simplearchitecture.plugin.util.GENERATED_FILE_COMMENT
import kim.jeonghyeon.simplearchitecture.plugin.util.getApiImplementationName
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

class ApiCreatingFunctionGenerator(
    private val options: PluginOptions,
    private val apiInterfacesName: Set<ClassName>
) {

    var isExpectFunctionCreated = AtomicBoolean(false)
    fun generateApiCreatingFunction() {
        if (options.hasCommon() && !isExpectFunctionCreated.getAndSet(true)) {
            createApiCreatingExpectFunctionFile()
        }

        createApiCreatingActualFunction()
    }

    private fun createApiCreatingExpectFunctionFile() {
        //get common path on generated path
        val commonPath = generatedSourceSetPath(options.buildPath, SOURCE_SET_NAME_COMMON)

        //create file
        FileSpec.builder(API_CREATION_FUNCTION_PACKAGE_NAME, API_CREATION_FUNCTION_FILE_NAME)
            .addComment(GENERATED_FILE_COMMENT)
            .addFunction(getApiCreatingExpectFunction())
            .build()
            .writeTo(File(commonPath))
    }

    private fun createApiCreatingActualFunction() {
        //get generated path on each variants

        //create file
        FileSpec.builder(API_CREATION_FUNCTION_PACKAGE_NAME, API_CREATION_FUNCTION_FILE_NAME)
            .addComment(GENERATED_FILE_COMMENT)
            .addFunction(getApiCreatingActualFunction())
            .build()
            .writeTo(File(options.getGeneratedTargetVariantsPath()))
    }

    private fun getApiCreatingExpectFunction() =
        FunSpec.builder("create")
            .receiver(ClassName("io.ktor.client", "HttpClient"))
            .addModifiers(KModifier.EXPECT, KModifier.INLINE)
            .addTypeVariable(TypeVariableName("reified T"))
            .addParameter("baseUrl", String::class)
            .returns(TypeVariableName("T"))
            .build()

    private fun getApiCreatingActualFunction() =
        FunSpec.builder("create")
            .receiver(ClassName("io.ktor.client", "HttpClient"))
            .addModifiers(KModifier.INLINE)
            .apply { if (options.hasCommon()) addModifiers(KModifier.ACTUAL) }
            .addTypeVariable(TypeVariableName("reified T"))
            .addParameter("baseUrl", String::class)
            .addApiCreatingStatement()
            .returns(TypeVariableName("T"))
            .build()

    private fun FunSpec.Builder.addApiCreatingStatement() = apply {
        addStatement(
            """
return when (T::class) {
        """
        )//if there is no new line after {, crash occurs

        apiInterfacesName.forEach {
            val interfaceName = ClassName(it.packageName, it.simpleName)
            val implementationName =
                ClassName(it.packageName, getApiImplementationName(it.simpleName))
            addStatement("%T::class -> %T(this, baseUrl) as T\n", interfaceName, implementationName)
        }

        addStatement(
            """    else -> error("can not create " + T::class.qualifiedName)
            |}""".trimMargin()
        )
    }
}