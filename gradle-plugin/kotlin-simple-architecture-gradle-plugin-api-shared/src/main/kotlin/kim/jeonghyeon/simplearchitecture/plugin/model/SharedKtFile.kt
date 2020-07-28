package kim.jeonghyeon.simplearchitecture.plugin.model

import kotlin.reflect.KClass

interface SharedKtFile {
    fun getChildrenOfKtClass(): Array<SharedKtClass>
    val packageFqName: String
    fun <T : Any> hasImport(clazz: KClass<T>): Boolean
}

interface SharedKtClass {
    val name: String
    fun isInterface(): Boolean
    fun <T : Any> hasAnnotation(clazz: KClass<T>): Boolean
    val packageName: String? // ex) kim.jeonghyeon.test
    val importSourceCode: String

    val functions: List<SharedKtNamedFunction>
    val superTypeText: String?

    val containingKtFile: SharedKtFile

    fun hasCompanionPropertyName(propertyName: String): Boolean
}

interface SharedKtNamedFunction {
    val name: String?
    val nameAndPrefix: String
    val parameters: Array<SharedKtParameter>
    fun hasBody(): Boolean
    fun isSuspend(): Boolean
    val returnTypeName: String?
}

interface SharedKtParameter {
    val nameAndType: String
    val name: String?
}
