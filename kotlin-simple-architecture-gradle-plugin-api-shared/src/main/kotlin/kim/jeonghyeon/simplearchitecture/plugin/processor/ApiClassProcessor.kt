package kim.jeonghyeon.simplearchitecture.plugin.processor

import com.squareup.kotlinpoet.*
import kim.jeonghyeon.simplearchitecture.plugin.model.ClassElement
import kim.jeonghyeon.simplearchitecture.plugin.model.ClassElementFindListener
import kim.jeonghyeon.simplearchitecture.plugin.model.PluginOptions
import kim.jeonghyeon.simplearchitecture.plugin.model.generatedPath
import kim.jeonghyeon.simplearchitecture.plugin.util.*
import org.jetbrains.kotlin.backend.common.descriptors.isSuspend
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

class ApiClassProcessor(val options: PluginOptions) :
    ClassElementFindListener {

    //todo this Api class is used by both gradle plugin and source.
    // so, I tried to make different gradle module with multiplatform
    // but same error occurs with the below. I followed the suggestion there.
    // but It was not working. so, I just hardcoded the Api class name
    // https://youtrack.jetbrains.com/issue/KT-31641
    // the reason seems that kapt can't figure out proper dependency between common and jvm
    // How about adding this as well? `attribute(KotlinPlatformType.attribute, KotlinPlatformType.jvm)`
    private val apiAnnotationName = "kim.jeonghyeon.annotation.Api"

    private var isStarted = AtomicBoolean(false)

    /**
     * this is for creating HttpClient.create<T>() function
     */
    private val apiInterfacesName: MutableSet<ClassName> = mutableSetOf()

    private val apiCreatingFunctionGenerator =
        ApiCreatingFunctionGenerator(options, apiInterfacesName)

    override fun onClassElementFound(element: ClassElement) {
        deleteGeneratedPathBeforeStart()

        if (element.isValid()) {
            element.createClassFile()

            apiCreatingFunctionGenerator.generateApiCreatingFunction()
        }

        //todo It was difficult to find when retrieving is finished.
        // so, create file whenever interface implementation file is created.
        // I tested [ClassBuilderInterceptorExtension]. but it's called sometimes after retrieving, somtimes before retrieving. somtimes both.
        // as it's not stable, I didn't use it
    }

    private fun deleteGeneratedPathBeforeStart() {
        //todo consider to add delete task and let it depends on compile task
        // 1. how to find android platform compilations
        // 2. how to find kotlin platform compilations
        // 3. if this is working, [ClassElement.path] is not required. so, [ClassElementFinder] can be moved to shared module and merge with [ClassElementRetrievalFinishDetector]
        // the below is how to apply delete task on multiplatform. looks more complicated than current way
        // tasks.create(TASK_CLEAN, Delete::class.java) {
        //        delete(File(generatedPath))
        //    }
        // // Multiplatform project.
        //    multiplatformExtension?.let { ext ->
        //        ext.targets.forEach { target ->
        //            //KotlinCompilation
        //            // compilationName : debug
        //            // compileKotlinTaskName : compileDebugKotlinAndroid
        //            // defaultSourceSetName : androidDebug
        //            // kotlinSourceSets : [source set androidDebug, source set commonMain]
        //            // platformType : andridJvm
        //            // moduleName : common_debug
        //            // name : debug
        //            target.compilations.forEach {
        //                it.compileKotlinTask.dependsOn(TASK_CLEAN)
        //            }
        //        }
        //        return
        //    }
        if (isStarted.getAndSet(true)) return

        File(options.getGeneratedTargetVariantsPath()).deleteRecursively()
    }

    private fun ClassElement.isValid(): Boolean = hasAnnotation(apiAnnotationName)
            && classDescriptor.modality == Modality.ABSTRACT//Todo limitation : can't detect if it's abstract class or interface
            && isTopLevelClass
            && !path.startsWith(
        generatedPath(options.buildPath)
    )

    private fun ClassElement.createClassFile() {
        FileSpec.builder(packageName, getApiImplementationName(simpleName))
            .addComment(GENERATED_FILE_COMMENT)
            .addType(asClassSpec())
            .build()
            .writeTo(File(options.getGeneratedTargetVariantsPath()))
    }

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

    private fun ClassElement.asClassSpec(): TypeSpec {
        //classDescriptor member information
        // name : class's name
        // modality : ABSTRACT for interface and abstract class
        // module.simpleName() : "$module_$build_type" ex)common_release
        // containingDeclaration.name : package's last folder's name
        // containingDeclaration.fqNameSafe.asString() : package full name. ex) kim.jeonghyeon.simplearchitecture.plugin
        // containingDeclaration.platform : [{"targetVersion":"JVM_1_8","targetPlatformVersion":{},"platformName":"JVM"}]
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

    private fun CallableMemberDescriptor.isValidFunction(): Boolean = modality == Modality.ABSTRACT

    private fun CallableMemberDescriptor.asFunctionSpec(
        packageName: String,
        className: String
    ): FunSpec {
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
            .addParameters(valueParameters.map {
                ParameterSpec.builder(
                    it.name.asString(),
                    it.type.asTypeName()
                ).build()
            })
            .addApiStatements(this, packageName, className)
        returnType?.asTypeName()?.let {
            builder.returns(it)
        }

        return builder.build()
    }

    private fun FunSpec.Builder.addApiStatements(
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



        addStatement("val mainPath = \"${packageName.replace(".", "-")}/${className}\"")
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