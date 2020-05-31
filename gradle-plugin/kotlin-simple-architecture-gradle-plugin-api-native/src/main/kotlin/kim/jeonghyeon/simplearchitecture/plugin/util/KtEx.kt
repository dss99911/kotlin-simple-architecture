package kim.jeonghyeon.simplearchitecture.plugin.util

import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType

/**
 * several cases for reference
 * com.package.ClassName
 * import com.package.ClassName -> ClassName
 * import com.package.ClassName -> com.package.ClassName
 *
 * !!LIMITATION!! we ignore the case below, as we don't know the package.
 * ClassName (if ClassName class is in root package)
 * ClassName (if ClassName class is in same package)
 */
inline fun <reified T> KtClass.hasAnnotation(): Boolean {
    val hasApiImport = containingKtFile.hasImport<T>()

    return annotationEntryList.any {
        (it.text == "@${T::class.simpleName}" && hasApiImport)
                || it.text == "@${T::class.qualifiedName}"
    }
}

inline fun <reified T> KtFile.hasImport() = importList?.imports?.any {
    it.importPath?.pathStr == T::class.qualifiedName
} ?: false

fun KtFile.getPathStringOf(type: String) =
    importList?.imports
        ?.firstOrNull { it.importPath?.importedName?.asString() == type }
        ?.importPath?.pathStr

fun KtNamedFunction.isSuspend() = text.substringBefore("(").contains("suspend")

val KtClass.importList get() = parent.getChildOfType<KtImportList>()!!

val KtClass.annotationEntryList
    get() =
        getChildOfType<KtDeclarationModifierList>()
            ?.getChildrenOfType<KtAnnotationEntry>() ?: arrayOf()

val KtNamedFunction.returnTypeName: String? get() = typeReference?.text

val KtClass.functions: List<KtNamedFunction> get() = declarations.mapNotNull { it as? KtNamedFunction? }

/**
 * ex) suspend fun getName
 */
val KtNamedFunction.nameAndPrefix: String get() = text.substringBefore("(")

val KtNamedFunction.parameters: Array<KtParameter>
    get() = valueParameterList?.getChildrenOfType() ?: emptyArray()

/**
 * ex) int: Int = 1 => int: Int
 */
val KtParameter.nameAndType: String get() = text.substringBefore("=")