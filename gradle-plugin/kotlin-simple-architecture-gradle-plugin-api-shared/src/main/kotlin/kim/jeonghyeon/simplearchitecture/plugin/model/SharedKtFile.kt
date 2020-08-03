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
    fun <T : Any> getAnnotationString(clazz: KClass<T>): String?//ex) @Api("dsfdsf")
    val packageName: String? // ex) kim.jeonghyeon.test
    val importSourceCode: String

    val functions: List<SharedKtNamedFunction>
    val superTypeText: String?

    val containingKtFile: SharedKtFile

    fun hasCompanionPropertyName(propertyName: String): Boolean

    fun <T : Any> hasAnnotation(clazz: KClass<T>): Boolean = getAnnotationString(clazz) != null
}

interface SharedKtNamedFunction {
    val name: String
    val nameAndPrefix: String
    val parameters: Array<SharedKtParameter>
    fun hasBody(): Boolean
    fun isSuspend(): Boolean
    val returnTypeName: String?
    fun <T : Any> getAnnotationString(clazz: KClass<T>): String?//ex) @Api("dsfdsf")
}

interface SharedKtParameter {
    val type: String
    val name: String?
    fun <T : Any> getAnnotationString(clazz: KClass<T>): String?//ex) @Api("dsfdsf")

}



/**
 * @Api("ddsf", true) ==> "ddsf", true
 */
fun String.getAnnotationParameterString(): String? {
    val parenthesisStartIndex = indexOf("(")
    val parenthesisEndIndex = lastIndexOf(")")
    if (parenthesisStartIndex == -1 || parenthesisEndIndex == -1) {
        return null
    }

    return substring(parenthesisStartIndex + 1, parenthesisEndIndex)
}

/**
 * @Api("ddsf", true) ==> Api
 */
fun String.getAnnotationName(): String {
    val startIndexOfName = indexOf("@")
    val endIndexOfName = if (indexOf("(") == -1) length else indexOf("(")
    if (startIndexOfName == -1) error("annotation string doesn't contains @")

    return substring(startIndexOfName + 1, endIndexOfName).trim()
}
/**
 * @Api("ddsf", true) ==> ddsf
 *
 * Limitation : doesn't support const string.
 * todo : consider several parameters, and also parameters' order is different
 */
fun String.getAnnotationParameterStringLiteral(): String? {
    if (!contains("\"")) {
        return null
    }
    if (contains("\"\"\"")) {
        return substring(indexOf("\"\"\"") + 3, lastIndexOf("\"\"\""))
    }
    return substring(indexOf("\"") + 1, lastIndexOf("\""))
}