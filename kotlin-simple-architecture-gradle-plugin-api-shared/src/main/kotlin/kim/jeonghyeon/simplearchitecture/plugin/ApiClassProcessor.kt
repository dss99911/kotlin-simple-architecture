package kim.jeonghyeon.simplearchitecture.plugin

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import org.jetbrains.kotlin.backend.common.descriptors.isSuspend
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.isNullable
import java.io.File

class ApiClassProcessor(val options: PluginOptions) : ClassElementRetrievalListener {

    //todo this Api class is used by both gradle plugin and source.
    // so, I tried to make different gradle module with multiplatform
    // but same error occurs with the below. I followed the suggestion there.
    // but It was not working. so, I just hardcoded the Api class name
    // https://youtrack.jetbrains.com/issue/KT-31641
    // the reason seems that kapt can't figure out proper dependency between common and jvm
    // How about adding this as well? `attribute(KotlinPlatformType.attribute, KotlinPlatformType.jvm)`
    val apiAnnotationName = "kim.jeonghyeon.annotation.Api"

    val generatedSourceSetPaths: MutableSet<String> = mutableSetOf()

    val apiInterfacesName: MutableSet<ClassName> = mutableSetOf()

    override fun onClassElementFound(element: ClassElement) {
        if (element.isValid()) {
            element.deletePathBeforeGenerating()
            element.createClassFile()
        }
    }

    override fun onRetrievalFinished() {
        if (options.hasCommon()) {
            createApiCreationExpectFunctionFile()
        }

        createApiCreationActualFunction()
    }

    //todo separate file for api creation function.
    // consider others as well. if it's able to separate
    fun createApiCreationExpectFunctionFile() {
        //get common path on generated path
        val commonPath = generatedSourceSetPath(options.buildPath, SOURCE_SET_NAME_COMMON)

        //create file
        FileSpec.builder(API_CREATION_FUNCTION_PACKAGE_NAME, API_CREATION_FUNCTION_FILE_NAME)
            .addComment(GENERATED_FILE_COMMENT)
            .addFunction(getApiCreationExpectFunction())
            .build()
            .writeTo(File(commonPath))
    }

    fun createApiCreationActualFunction() {
        //get generated path on each variants
        val targetVariantsNamed =
            generatedSourceSetPath(options.buildPath, options.compileTargetVariantsName)

        //create file
        FileSpec.builder(API_CREATION_FUNCTION_PACKAGE_NAME, API_CREATION_FUNCTION_FILE_NAME)
            .addComment(GENERATED_FILE_COMMENT)
            .addFunction(getApiCreationActualFunction())
            .build()
            .writeTo(File(targetVariantsNamed))

    }

    private fun getApiCreationExpectFunction() =
        FunSpec.builder("create")
            .receiver(ClassName("io.ktor.client", "HttpClient"))
            .addModifiers(KModifier.EXPECT, KModifier.INLINE)
            .addTypeVariable(TypeVariableName("reified T"))
            .addParameter("baseUrl", String::class)
            .returns(TypeVariableName("T"))
            .build()

    private fun getApiCreationActualFunction() =
        FunSpec.builder("create")
            .receiver(ClassName("io.ktor.client", "HttpClient"))
            .addModifiers(KModifier.INLINE)
            .apply { if (options.hasCommon()) addModifiers(KModifier.ACTUAL) }
            .addTypeVariable(TypeVariableName("reified T"))
            .addParameter("baseUrl", String::class)
            .addApiCreationStatement()
            .returns(TypeVariableName("T"))
            .build()

    private fun FunSpec.Builder.addApiCreationStatement() = apply {

        //todo check generated code's indent

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
            """
            |    else -> error("can not create " + T::class.qualifiedName)
            |}""".trimMargin()
        )
    }

    /**
     * need to delete previous generated source.
     *
     * when you see databinding library.
     * it adds source to each variants
     *
     * but this is multiplatform.
     * if we create source depending on variants
     * the generated code can be referred by only on the variant.
     * it means that. if compile android Debug, common source set can not refer android's generated source code.
     *
     * so, generated source is based on source set.
     * but we are not sure when generated code should be deleted before compile
     * because same source set's source is called several times as each compile use multiple source set.
     *
     * so, I decided to delete the source before creating new source.
     */
    private fun ClassElement.deletePathBeforeGenerating() {
        val path = getGeneratedSourceSetPath()
        if (generatedSourceSetPaths.contains(path)) {
            return
        }

        generatedSourceSetPaths.add(path)
        File(path).deleteRecursively()
    }

    private fun ClassElement.isValid(): Boolean = hasAnnotation(apiAnnotationName)
            && classDescriptor.modality == Modality.ABSTRACT//Todo limitation : can't detect if it's abstract class or interface
            && isTopLevelClass
            && !path.startsWith(generatedPath(options.buildPath))

    private fun ClassElement.createClassFile() {
        FileSpec.builder(packageName, getApiImplementationName(simpleName))
            .addComment(GENERATED_FILE_COMMENT)
            .addType(asClassSpec())
            .addFunction(getApiConstructorFunction(packageName, simpleName))
            .build()
            .writeTo(File(getGeneratedSourceSetPath()))
    }

    fun ClassElement.getGeneratedSourceSetPath(): String {
        //find target by matching source folder
        val sourceSetName = options.sourceSets.firstOrNull {
            it.sourcePathSet.any {
                path.startsWith(it)
            }
        }?.name ?: guessSourceSetName(path)

        return generatedSourceSetPath(options.buildPath, sourceSetName)
    }

    fun getApiImplementationName(interfaceName: String) = interfaceName + "Impl"

    /**
     * fun SomeApi(client, baseUrl) {
     *    return SomApiImpl(client, baseUrl)
     * }
     */
    private fun getApiConstructorFunction(packageName: String, interfaceName: String) =
        FunSpec.builder(interfaceName)
            .addApiConstructorParameter()
            .returns(ClassName(packageName, interfaceName))
            .addStatement("return ${getApiImplementationName(interfaceName)}(client, baseUrl) ")
            .build()

    private fun FunSpec.Builder.addApiConstructorParameter(): FunSpec.Builder =
        addParameter(
            ParameterSpec.builder("client", ClassName("io.ktor.client", "HttpClient")).build()
        )
            .addParameter("baseUrl", String::class)

    private fun TypeSpec.Builder.addApiConstructorProperty(): TypeSpec.Builder =
        addProperty(
            PropertySpec.builder("client", ClassName("io.ktor.client", "HttpClient"))
                .initializer("client")
                .build()
        )
            .addProperty(
                PropertySpec.builder("baseUrl", String::class)
                    .initializer("baseUrl")
                    .build()
            )

    /**
     * !! LIMITATION !!
     * 1. for native, [ApiGradleSubplugin.apply] is called before 'ios', 'mobile' sourceSet is created.
     * 2. so, It's difficult to figure out which sourceSet a class belongs to on native
     * 3. in this case, we guess sourceSetName by path
     */
    private fun guessSourceSetName(path: String): String {
        val srcPath = "${options.projectPath}/src/"
        if (!path.startsWith(srcPath)) {
            error("sourceSet is not recognized and also failed to guess : $path")
        }

        return path.replaceFirst(srcPath, "")
            .replaceAfter("/", "")
            .also {
                println("guessed : $it")
            }
    }

    fun ClassElement.asClassSpec(): TypeSpec {
        //classDescriptor member information
        // name : class's name
        // modality : ABSTRACT for interface and abstract class
        // module.simpleName() : "$module_$build_type" ex)common_release
        // containingDeclaration.name : package's last folder's name
        // containingDeclaration.fqNameSafe.asString() : package full name. ex) kim.jeonghyeon.simplearchitecture.plugin
        // containingDeclaration.platform : [{"targetVersion":"JVM_1_8","targetPlatformVersion":{},"platformName":"JVM"}] todo check if other platform?
        // containingDeclaration.parents.joinToString(","){it.name.asString() : "\u003ccommon_release\u003e". able to check it's build type debug or release
        // source.containingFile.name : file name including '.kt'

        val interfaceName = ClassName(packageName, simpleName).also {
            apiInterfacesName.add(it)
        }

        return TypeSpec.classBuilder(ClassName(packageName, getApiImplementationName(simpleName)))
            .addSuperinterface(interfaceName)
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addApiConstructorParameter()
                    .build()
            ).addApiConstructorProperty()
            .addFunctions(functions()
                .filter { it.isValidFunction() }
                .map { it.asFunctionSpec(packageName, simpleName) }
            ).build()
    }

    fun CallableMemberDescriptor.isValidFunction(): Boolean = modality == Modality.ABSTRACT

    fun CallableMemberDescriptor.asFunctionSpec(packageName: String, className: String): FunSpec {
        //name : function's name
        //origin.name : same with `name`
        //source.containingFile.name : file name including '.kt'
        //valueParameters[0].type.toString() : parameter's type
        //returnType?.memberScope?.getFunctionNames()?.joinToString { it.asString() } : shows functions of the type
        //returnType?.memberScope?.getVariableNames()?.joinToString { it.asString() } : shows variables of the type
        check(isSuspend) { "@Api : abstract function should be suspend on $className.$name'" }

        val builder = FunSpec.builder(name.toString())
            .addModifiers(KModifier.SUSPEND)
            .addModifiers(KModifier.OVERRIDE)
            .addParameters(valueParameters.map { it.asParameterSpec() })
            .addApiStatements(this, packageName, className)
        returnType?.asTypeName()?.let {
            builder.returns(it)
        }

        return builder.build()
    }

    fun FunSpec.Builder.addApiStatements(
        funDescriptor: CallableMemberDescriptor,
        packageName: String,
        className: String
    ) = apply {
        val parametersJsonString = funDescriptor.valueParameters.joinToString("\n|          ") {
            """"${it.name.asString()}" to ${it.name.asString()} """
        }

        val returnClassName = funDescriptor.returnType
            ?.takeIf { it.packageName != "kotlin" || it.name != "Unit" }
            ?.let { ClassName(it.packageName, it.name) }

        val post = MemberName("io.ktor.client.request", "post")
        val contentType = MemberName("io.ktor.http", "contentType")
        val ContentType = ClassName("io.ktor.http", "ContentType")
        val json = MemberName("kotlinx.serialization.json", "json")
        val throwException = MemberName("kim.jeonghyeon.common.net", "throwException")
        val validateResponse = MemberName("kim.jeonghyeon.common.net", "validateResponse")
        val Json = ClassName("kotlinx.serialization.json", "Json")
        val JsonConfiguration = ClassName("kotlinx.serialization.json", "JsonConfiguration")
        val HttpResponse = ClassName("io.ktor.client.statement", "HttpResponse")
        val readText = MemberName("io.ktor.client.statement", "readText")



        addStatement("val mainPath = \"${packageName.replace(".", "_")}/${className}\"")
        addStatement("val subPath = \"${funDescriptor.name}\"")
        addStatement("val baseUrlWithoutSlash = if (baseUrl.last() == '/') baseUrl.take(baseUrl.lastIndex) else baseUrl")
        addStatement(
            """
            |val response = try {
            |   client.%M<%T>(baseUrlWithoutSlash + "/" + mainPath + "/" + subPath) {
            |       %M(%T.Application.Json)
            |
            |       body = %M {
            |           $parametersJsonString                                    
            |       }
            |   }
            |} catch (e: Exception) {
            |   client.%M(e)
            |}
            |""".trimMargin(), post, HttpResponse, contentType, ContentType, json, throwException
        )

        addStatement(
            """
            |client.%M(response)
            |val json = %T(%T.Stable)
            |""".trimMargin(), validateResponse, Json, JsonConfiguration
        )

        if (returnClassName != null) {
            addStatement(
                "return json.parse(%T.serializer(), response.%M())",
                returnClassName,
                readText
            )
        }

    }
}

private fun ValueParameterDescriptor.asParameterSpec(): ParameterSpec =
    ParameterSpec.builder(name.asString(), type.asTypeName()).build()

fun KotlinType.asTypeName(): TypeName {
    val className: ClassName = createClassName().let {
        if (isNullable()) it.copy(true) else it
    }

    if (arguments.isNotEmpty()) {
        return arguments
            .map { it.type.asTypeName() }
            .let { className.parameterizedBy(*it.toTypedArray()) }
    }
    return className
}

fun KotlinType.createClassName(): ClassName {
    //on Jvm, packageName is java.util, instead of kotlin, even if source set is common
    //todo currently only HashMap is checked.
    // need to check other standard classes
    if (packageName == "java.util") {
        if (name == "HashMap") {
            return ClassName("kotlin.collections", name)
        }
    }

    return ClassName(
        packageName, name
    )
}

val KotlinType.packageName: String get() = getJetTypeFqName(false).substringBeforeLast(".")
val KotlinType.name: String get() = getJetTypeFqName(false).substringAfterLast(".")

private const val GENERATED_FILE_COMMENT = "GENERATED by Simple Api Plugin"
private const val API_CREATION_FUNCTION_PACKAGE_NAME = "kim.jeonghyeon.common.net"
private const val API_CREATION_FUNCTION_FILE_NAME = "HttpClientEx"