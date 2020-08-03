package kim.jeonghyeon.simplearchitecture.plugin.impl

import kim.jeonghyeon.simplearchitecture.plugin.model.SharedKtClass
import kim.jeonghyeon.simplearchitecture.plugin.model.SharedKtFile
import kim.jeonghyeon.simplearchitecture.plugin.model.SharedKtNamedFunction
import kim.jeonghyeon.simplearchitecture.plugin.model.SharedKtParameter
import kim.jeonghyeon.simplearchitecture.plugin.util.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType
import kotlin.reflect.KClass

fun KtFile.asShared(): SharedKtFile {
    return SharedKtFileImpl(this)
}

class SharedKtFileImpl(val ktFile: KtFile) : SharedKtFile {
    override val packageFqName: String get() = ktFile.packageFqName.asString()

    override fun getChildrenOfKtClass(): Array<SharedKtClass> = ktFile.getChildrenOfType<KtClass>().map {
        SharedKtClassImpl(it)
    }.toTypedArray()

    override fun <T : Any> hasImport(clazz: KClass<T>): Boolean = ktFile.hasImport(clazz)
}

class SharedKtClassImpl(val ktClass: KtClass) : SharedKtClass {

    override val name: String
        get() = ktClass.name!!
    override val containingKtFile: SharedKtFile
        get() = SharedKtFileImpl(ktClass.containingKtFile)

    //todo if package is empty?
    override val packageName: String?
        get() = ktClass.parent.getChildOfType<KtPackageDirective>()?.fqName?.asString()

    //todo if import is nuLL
    override val importSourceCode: String
        get() = ktClass.importList.text

    override fun isInterface(): Boolean = ktClass.isInterface()

    override fun <T : Any> getAnnotationString(clazz: KClass<T>) = ktClass.getAnnotationString(clazz)

    override val functions: List<SharedKtNamedFunction>
        get() = ktClass.functions.map { SharedKtNamedFunctionImpl(it) }
    override val superTypeText: String?
        get() = ktClass.findDescendantOfType<KtSuperTypeList>()?.text

    override fun hasCompanionPropertyName(propertyName: String): Boolean {
        return ktClass.findDescendantOfType<KtObjectDeclaration> { it.isCompanion() }
            ?.findDescendantOfType<KtProperty> { it.name == propertyName } != null
    }
}

class SharedKtNamedFunctionImpl(val ktFunc: KtNamedFunction) : SharedKtNamedFunction {
    override fun hasBody(): Boolean = ktFunc.hasBody()
    override fun isSuspend(): Boolean = ktFunc.isSuspend()

    override val nameAndPrefix: String
        get() = ktFunc.nameAndPrefix
    override val parameters: Array<SharedKtParameter>
        get() = ktFunc.parameters.map { SharedKtParameterImpl(it) }.toTypedArray()
    override val returnTypeName: String?
        get() = ktFunc.returnTypeName

    override val name: String
        get() = ktFunc.name!!

    override fun <T : Any> getAnnotationString(clazz: KClass<T>): String? = ktFunc.getAnnotationString(clazz)
}

class SharedKtParameterImpl(val parameter: KtParameter) : SharedKtParameter {
    override val type: String
        get() = parameter.type
    override val name: String?
        get() = parameter.name

    override fun <T : Any> getAnnotationString(clazz: KClass<T>): String? =
        parameter.getAnnotationString(clazz)
}