package kim.jeonghyeon.simplearchitecture.plugin.util

import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.com.intellij.openapi.vfs.StandardFileSystems
import org.jetbrains.kotlin.com.intellij.openapi.vfs.VirtualFileManager
import org.jetbrains.kotlin.com.intellij.psi.PsiManager
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
 * todo currently we ignore the case below, as we don't know the package. need to check class's package
 * ClassName (if ClassName class is in root package)
 * ClassName (if ClassName class is in same package)
 */
fun <T : Any> KtClass.getAnnotationString(clazz: KClass<T>): String? {
    val hasApiImport = containingKtFile.hasImport(clazz)

    return annotationEntryList.firstOrNull {
        (it.text.startsWith("@${clazz.simpleName}") && hasApiImport)
                || it.text.startsWith("@${clazz.qualifiedName}")
    }?.text
}
fun <T : Any> KtNamedFunction.getAnnotationString(clazz: KClass<T>): String? {
    val hasApiImport = containingKtFile.hasImport(clazz)

    return annotationEntryList.firstOrNull {
        (it.text.startsWith("@${clazz.simpleName}") && hasApiImport)
                || it.text.startsWith("@${clazz.qualifiedName}")
    }?.text
}

fun <T : Any> KtProperty.getAnnotationString(clazz: KClass<T>): String? {
    val hasApiImport = containingKtFile.hasImport(clazz)

    return annotationEntryList.firstOrNull {
        (it.text.startsWith("@${clazz.simpleName}") && hasApiImport)
                || it.text.startsWith("@${clazz.qualifiedName}")
    }?.text
}

fun <T : Any> KtParameter.getAnnotationString(clazz: KClass<T>): String? {
    val hasApiImport = containingKtFile.hasImport(clazz)

    return annotationEntryList.firstOrNull {
        (it.text.startsWith("@${clazz.simpleName}") && hasApiImport)
                || it.text.startsWith("@${clazz.qualifiedName}")
    }?.text
}


fun <T : Any> KtFile.hasImport(clazz: KClass<T>) = importList?.imports?.any {
    it.importPath?.pathStr == clazz.qualifiedName ||
            it.importPath?.pathStr == clazz.qualifiedName?.replaceAfterLast(".", "*")
} ?: false

fun KtFile.getPathStringOf(type: String) =
    importList?.imports
        ?.firstOrNull { it.importPath?.importedName?.asString() == type }
        ?.importPath?.pathStr

//todo ignore annotation
fun KtNamedFunction.isSuspend() = text.substringBefore("fun").contains("suspend") || text.contains(
    Regex("suspend +fun")
)

val KtClass.importList get() = parent.getChildOfType<KtImportList>()!!

val KtClass.annotationEntryList get() = getChildOfType<KtDeclarationModifierList>()?.getChildrenOfType<KtAnnotationEntry>() ?: arrayOf()

val KtNamedFunction.annotationEntryList get() = getChildOfType<KtDeclarationModifierList>()?.getChildrenOfType<KtAnnotationEntry>() ?: arrayOf()
val KtProperty.annotationEntryList get() = getChildOfType<KtDeclarationModifierList>()?.getChildrenOfType<KtAnnotationEntry>() ?: arrayOf()

val KtParameter.annotationEntryList get() = getChildOfType<KtDeclarationModifierList>()?.getChildrenOfType<KtAnnotationEntry>() ?: arrayOf()

val KtNamedFunction.returnTypeName: String? get() = typeReference?.text

val KtClass.functions: List<KtNamedFunction> get() = declarations.mapNotNull { it as? KtNamedFunction? }

/**
 * ex) suspend fun getName
 */
val KtNamedFunction.nameAndPrefix: String get() = text.substringBeforeLast("(")

val KtNamedFunction.parameters: Array<KtParameter>
    get() = valueParameterList?.getChildrenOfType() ?: emptyArray()

/**
 * ex) @Body int: Int = 1 =>  Int
 */
val KtParameter.type: String get() = text.substringAfterLast(":").substringBefore("=")

fun File.toKtFile(project: Project): KtFile {
    val fileSystem =
        VirtualFileManager.getInstance().getFileSystem(StandardFileSystems.FILE_PROTOCOL)
    val psiManager = PsiManager.getInstance(project)

    return (fileSystem.findFileByPath(absolutePath)
        ?: error("can't fine virtual file : $absolutePath"))
        .let { psiManager.findFile(it) ?: error("can't fine psi file : $absolutePath") }
        .let { KtFile(it.viewProvider, false) }
}