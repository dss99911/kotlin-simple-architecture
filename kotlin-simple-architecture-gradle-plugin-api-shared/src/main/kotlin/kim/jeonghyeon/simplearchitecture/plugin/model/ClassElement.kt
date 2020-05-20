package kim.jeonghyeon.simplearchitecture.plugin.model

import org.jetbrains.kotlin.backend.common.serialization.findPackage
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.lazy.descriptors.LazyClassDescriptor

data class ClassElement(
    val simpleName: String,//simpleName : class's name. ex)ApiTest3
    val path: String,//absolute path of class's file. ex) ~/User.kt
    val packageName: String,//package full name. ex)kim.jeonghyeon.common.net
    val classDescriptor: ClassDescriptor
) {
    fun hasAnnotation(fqName: String): Boolean =
        classDescriptor.annotations.findAnnotation(FqName(fqName)) != null

    val isTopLevelClass get(): Boolean = classDescriptor.containingDeclaration.fqNameSafe.asString() == classDescriptor.findPackage().fqName.asString()

    fun functions(): Collection<CallableMemberDescriptor> = functions(
        CallableMemberDescriptor.Kind.DECLARATION
    )

    fun functions(kind: CallableMemberDescriptor.Kind): Collection<CallableMemberDescriptor> {
        return (classDescriptor as LazyClassDescriptor)
            .declaredCallableMembers
            .filter { it.kind == kind }
            .filterIsInstance<SimpleFunctionDescriptor>()
    }
}

interface ClassElementFindListener {
    fun onClassElementFound(element: ClassElement)
}