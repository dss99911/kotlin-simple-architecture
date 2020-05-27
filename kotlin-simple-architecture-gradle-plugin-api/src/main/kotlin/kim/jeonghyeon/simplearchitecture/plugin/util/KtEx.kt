package kim.jeonghyeon.simplearchitecture.plugin.util

import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType

//todo if same package with [Api] then api impl is not created
inline fun <reified T> KtClass.hasAnnotation(): Boolean {
    val hasApiImport = importList.imports.any {
        it.importPath?.pathStr == T::class.qualifiedName
    }

    return annotationEntryList.any {
        (it.text == "@${T::class.simpleName}" && hasApiImport)
                || it.text == "@${T::class.qualifiedName}"
    }
}

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