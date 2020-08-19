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
    val ktClass: SharedKtClass?
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
fun String.trimParenthesis(): String? {
    val parenthesisStartIndex = indexOf("(")
    val parenthesisEndIndex = lastIndexOf(")")
    if (parenthesisStartIndex == -1 || parenthesisEndIndex == -1) {
        return null
    }

    return substring(parenthesisStartIndex + 1, parenthesisEndIndex)
}

/**
 * 1. "ddd", true => "ddd"
 * 2. path: "ddd", encoded: true
 *
 */
fun String.getParameterString(name: String, index: Int): String? {
    val params = split(",")//todo need to improve logic to split params
        .map { it.trim() }

    val namedParameter = params
        //has parameter name. todo need to improve logic to detect name exists
        .firstOrNull { it.startsWith(name) }
        ?.let { it.trimParameterName() }

    if (namedParameter != null) {
        return namedParameter
    }

    val indexNameExists = params
        //has other parameter name. todo need to improve logic to detect name exists
        .indexOfFirst { it.contains("=") }
        .let { if (it == -1) params.size else it }
    return if (index < indexNameExists) {
        if (params[index].isBlank()) {
            null
        } else {
            params[index]
        }
    } else null
}

/**
 * path: "ddd" => "ddd"
 */
fun String.trimParameterName(): String = substringAfter("=").trim()

/**
 * @Api("ddsf", true) ==> Api
 */
fun String.getAnnotationName(): String {
    val startIndexOfName = indexOf("@")
    val endIndexOfName = if (indexOf("(") == -1) length else indexOf("(")
    if (startIndexOfName == -1) error("annotation string doesn't contains @")

    return substring(startIndexOfName + 1, endIndexOfName).trim()
}