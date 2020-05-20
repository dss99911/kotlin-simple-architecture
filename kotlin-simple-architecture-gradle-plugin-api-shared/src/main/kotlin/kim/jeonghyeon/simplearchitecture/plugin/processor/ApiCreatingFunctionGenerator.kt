package kim.jeonghyeon.simplearchitecture.plugin.processor

import com.squareup.kotlinpoet.*
import kim.jeonghyeon.simplearchitecture.plugin.model.PluginOptions
import kim.jeonghyeon.simplearchitecture.plugin.model.SOURCE_SET_NAME_COMMON
import kim.jeonghyeon.simplearchitecture.plugin.model.generatedSourceSetPath
import java.io.File

class ApiCreatingFunctionGenerator(
    val options: PluginOptions,
    val apiInterfacesName: Set<ClassName>
) {
    fun generateApiCreatingFunction() {
        if (options.hasCommon()) {
            createApiCreatingExpectFunctionFile()
        }

        createApiCreatingActualFunction()
    }

    //todo separate file for api creation function.
    // consider others as well. if it's able to separate
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
        val targetVariantsNamed =
            generatedSourceSetPath(options.buildPath, options.compileTargetVariantsName)

        //create file
        FileSpec.builder(API_CREATION_FUNCTION_PACKAGE_NAME, API_CREATION_FUNCTION_FILE_NAME)
            .addComment(GENERATED_FILE_COMMENT)
            .addFunction(getApiCreatingActualFunction())
            .build()
            .writeTo(File(targetVariantsNamed))
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