package kim.jeonghyeon.simplearchitecture.plugin

import com.google.gson.FieldNamingStrategy
import com.google.gson.Gson
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import de.jensklingenberg.mpapt.common.canonicalFilePath
import de.jensklingenberg.mpapt.common.methods
import de.jensklingenberg.mpapt.common.simpleName
import de.jensklingenberg.mpapt.model.AbstractProcessor
import de.jensklingenberg.mpapt.model.Element
import de.jensklingenberg.mpapt.model.RoundEnvironment
import org.jetbrains.kotlin.backend.common.descriptors.isSuspend
import org.jetbrains.kotlin.backend.common.serialization.findPackage
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.platform.konan.isNative
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.resolve.isInlineClass
import org.jetbrains.kotlin.types.asSimpleType
import java.io.File
import java.lang.reflect.Field

class ApiClassProcessor(val buildPath: String, val sourceSets: List<SourceSetOption>) :
    AbstractProcessor() {

    private val apiClassName = Api::class.java.name
    var isNative: Boolean = false

    override fun getSupportedAnnotationTypes(): Set<String> = setOf(apiClassName)

    override fun process(roundEnvironment: RoundEnvironment) {
        log(
            "environment",
            roundEnvironment.module?.simpleName(),
            roundEnvironment.module?.platform?.toString(),
            sourceSets
        )

        isNative = roundEnvironment.module?.platform.isNative()

        //todo this is called several times. maybe because ApiComponentRegistrar calls several times. is it really required?
        //todo check if native class is also recognized
        roundEnvironment.getElementsAnnotatedWith(apiClassName).forEach { element ->
            when (element) {
                is Element.ClassElement -> {
                    //pack : package full name. ex)kim.jeonghyeon.common.net
                    //simpleName : class's name. ex)ApiTest3
                    //path : ""
                    if (element.classDescriptor.isValid()) {
                        element.classDescriptor.createClassFile()
                    }
                }
            }
        }
    }

    fun ClassDescriptor.isValid(): Boolean =
        modality == Modality.ABSTRACT
                && isTopLevelClass

    val ClassDescriptor.isTopLevelClass get(): Boolean = containingDeclaration.fqNameSafe.asString() == findPackage().fqName.asString()

    fun ClassDescriptor.createClassFile() {
        log("class name", source.containingFile.toString())
        log(
            "class",
            name,
            modality,
            defaultType.toString(),
            original.name,
            module.simpleName(),
            source.containingFile.name,
            this.source.containingFile::class.qualifiedName,
            canonicalFilePath(),
            isInlineClass(),
            isInner,
            isExternal,
            findPackage().name,
            findPackage().fqName,
            findPackage().fqNameSafe
        )

        containingDeclaration.isInlineClass()

        log("file", packageName, name.asString() + "Impl")
        FileSpec.builder(packageName, name.asString() + "Impl")
            .addComment("GENERATED by Simple Api Plugin")
            .addType(asClassSpec())
            .build()
            .writeTo(File(getGeneratedClassPath(packageName)))
        //todo how to set generated file's folder as source path.
    }

    fun ClassDescriptor.getGeneratedClassPath(packageName: String): String {
        //todo LIMITATION : it won't work properly on the below cases. need to find the way how to figure out build path.
        // class is only on some build type or flavor
        //Remove <> from module name, and remove '_release', '_debug' in case it exists.

        //todo how to detect target and compilation name?
        // on Native, it's not possible to get path.


        val sourceFolderName = if (isNative) NATIVE_TARGET_NAME else {
            val canonicalFilePath = canonicalFilePath()!!
            sourceSets.first {
                it.sourcePathSet.any {
                    canonicalFilePath.startsWith(it)
                }
            }.name
        }
        val packagePath = packageName.replace(".", "/")
        return "$buildPath/generated/source/simpleapi/$sourceFolderName/$packagePath"
    }

    //todo if class is inside other class? then containingDeclaration seems other class. if it's inner class. ignore it.
    //todo check findPackage()
    val ClassDescriptor.packageName get(): String = containingDeclaration.fqNameSafe.asString()


    fun ClassDescriptor.asClassSpec(): TypeSpec {

        //name : class's name
        //modality : ABSTRACT for interface and abstract class
        //module.simpleName() : "$module_$build_type" ex)common_release
        //containingDeclaration.name : package's last folder's name
        //containingDeclaration.fqNameSafe.asString() : package full name. ex) kim.jeonghyeon.simplearchitecture.plugin
        //containingDeclaration.platform : [{"targetVersion":"JVM_1_8","targetPlatformVersion":{},"platformName":"JVM"}] todo check if other platform?
        //containingDeclaration.parents.joinToString(","){it.name.asString() : "\u003ccommon_release\u003e". able to check it's build type debug or release
        //source.containingFile.name : file name including '.kt'

        //guessingBuildFolder() : /Users/hyun.kim/AndroidstudioProjects/my/androidLibrary/common/src/main/kotlin/kim/jeonghyeon/common/net/UserApi.ktbuild/
        //guessingProjectFolder() : /Users/hyun.kim/AndroidstudioProjects/my/androidLibrary/common/src/main/kotlin/kim/jeonghyeon/common/net/UserApi.kt
        //guessingProjectFolder() : "/Users/hyun.kim/AndroidstudioProjects/my/androidLibrary/common/src/main/kotlin/
        //canonicalFilePath() : /Users/hyun.kim/AndroidstudioProjects/my/androidLibrary/common/src/main/kotlin/kim/jeonghyeon/common/net/UserApi.kt
        val methods = methods()

        methods.forEach {
            log("method",
                it.modality,//todo check this, and if it's not abstract ignore it.
                it.returnType?.asSimpleType()?.toString(),//todo how to find package of the class?
                it.returnType?.getJetTypeFqName(false),
                it.returnType?.memberScope?.getClassifierNames()?.joinToString { it.asString() },
                it.returnType?.memberScope?.getFunctionNames()?.joinToString { it.asString() },
                it.returnType?.memberScope?.getVariableNames()?.joinToString { it.asString() },
                it.valueParameters.joinToString { it.type.toString() },//todo generic type's
                it.valueParameters.joinToString { it.name.toString() } //todo parameter's name
            )
        }

        val interfaceName = name.toString()
        return TypeSpec.classBuilder(ClassName(packageName, interfaceName + "Impl"))
            .addSuperinterface(ClassName(packageName, interfaceName))
            .addFunctions(methods.map { it.asFunctionSpec() })
            .build()
    }

    fun CallableMemberDescriptor.asFunctionSpec(): FunSpec {
        //name : function's name
        //origin.name : same with `name`
        //source.containingFile.name : file name including '.kt'
        //valueParameters[0].type.toString() : parameter's type
        //returnType?.memberScope?.getFunctionNames()?.joinToString { it.asString() } : shows functions of the type
        //returnType?.memberScope?.getVariableNames()?.joinToString { it.asString() } : shows variables of the type

        val builder = FunSpec.builder(name.toString())
        if (isSuspend) {
            builder.addModifiers(KModifier.SUSPEND)
        }
        builder.addModifiers(KModifier.OVERRIDE)

        //todo nullable, generic multiple times.
        builder.addParameters(valueParameters.map { it.asParameterSpec() })
//        builder.returns(this.returnType.)todo how to get type from kotlin type?
        return builder.build()
    }
}

private fun ValueParameterDescriptor.asParameterSpec(): ParameterSpec {

    val type = ClassName("kotlin.collections", "HashMap")
        .parameterizedBy(
            ClassName("kotlin", "String"),
            ClassName("kotlin", "Int").copy(true)
        )

    return ParameterSpec.builder(name.asString(), type).build()
}


fun log(vararg text: Any?) {
    println("TestHyun : ${Gson().toJson(text)}")
}

//TODO HYUN [multi-platform2] : change to other library
@Target(AnnotationTarget.CLASS)
annotation class Api
class GsonFieldNamingStrategy : FieldNamingStrategy {
    override fun translateName(field: Field?): String? {
        return "${field?.declaringClass?.canonicalName}.${field?.name}"
    }
}


//    val getFunctions = mutableListOf<FunctionDescriptor>()
//    override fun onProcessingOver() {
//        val getFunSpecs = getFunctions
//            .map { function ->
//                FunSpec.builder(function.simpleName()).apply {
//                    when (function.isSuspend) {
//                        true -> addModifiers(KModifier.SUSPEND)
//                    }
//                }
//                    .addModifiers(KModifier.OVERRIDE)
//                    .addParameters(
//                        function.getFunctionParameters().map {
//                            ParameterSpec.builder(
//                                it.parameterName,
//                                ClassName(it.packagee.packagename, it.packagee.classname)
//                            ).build()
//                        }
//
//                    )
//                    .returns(ClassName("", function.getReturnTypeImport().toString()))
//                    .addStatement("return client.get(baseUrl+${function.annotations.first().argumentValue("url")})")
//                    .build()
//            }
//
//
//        val file = FileSpec.builder(getFunctions.first().containingDeclaration.containingDeclaration?.fqNameSafe?.asString()
//            ?: "", "KtorfitApi")
//            .addComment("GENERATED by KtorFit")
//            .apply {
//                if (getFunctions.isNotEmpty()) {
//                    addImport("io.ktor.client.request.get", "")
//                }
//            }
//            .addType(
//                TypeSpec.classBuilder(
//                    ClassName(getFunctions.first().containingDeclaration.containingDeclaration?.fqNameSafe?.asString()
//                    ?: "", "KtorfitApi")
//                )
//                    .primaryConstructor(
//                        FunSpec.constructorBuilder()
//                        .addParameter("baseUrl", String::class)
//                        .build())
//                    .addProperty(PropertySpec.builder("client", ClassName("io.ktor.client", "HttpClient")).initializer("HttpClient()").build())
//                    .addProperty(PropertySpec.builder("baseUrl", String::class).initializer("baseUrl").build())
//
//                    .addSuperinterface(
//                        ClassName(
//                            getFunctions.first().containingDeclaration.containingDeclaration?.fqNameSafe?.asString()
//                                ?: "",
//                            "Api"
//                        )
//                    )
//                    .addFunctions(getFunSpecs)
//
//                    .addFunction(
//                        FunSpec.builder("create")
//                            .addCode(
//                                """
//        |return this
//        |""".trimMargin()
//
//
//                            ).returns(ClassName("", "Api")).build()
//                    )
//
//
//                    // .addFunctions(postFuncs)
//                    .build()
//            )
//
//
//            .build()
//        val filepath = (getFunctions.first().containingDeclaration as ClassDescriptor).guessingSourceSetFolder()
//        file.writeTo(File(filepath))
//    }