package kim.jeonghyeon.simplearchitecture.plugin.util

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.StandardFileSystems
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.psi.PsiManager
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType
import java.io.File
import kotlin.reflect.KClass

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
fun <T : Any> KtClass.hasAnnotation(clazz: KClass<T>): Boolean {
    val hasApiImport = containingKtFile.hasImport(clazz)

    return annotationEntryList.any {
        (it.text == "@${clazz.simpleName}" && hasApiImport)
                || it.text == "@${clazz.qualifiedName}"
    }
}

fun <T : Any> KtFile.hasImport(clazz: KClass<T>) = importList?.imports?.any {
    it.importPath?.pathStr == clazz.qualifiedName
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

fun File.toKtFile(project: Project): KtFile {
    val fileSystem =
        VirtualFileManager.getInstance().getFileSystem(StandardFileSystems.FILE_PROTOCOL)
    val psiManager = PsiManager.getInstance(project)

    return (fileSystem.findFileByPath(absolutePath)
        ?: error("can't fine virtual file : $absolutePath"))
        .let { psiManager.findFile(it) ?: error("can't fine psi file : $absolutePath") }
        .let { KtFile(it.viewProvider, false) }
}