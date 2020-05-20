package kim.jeonghyeon.simplearchitecture.plugin.util

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.isNullable

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