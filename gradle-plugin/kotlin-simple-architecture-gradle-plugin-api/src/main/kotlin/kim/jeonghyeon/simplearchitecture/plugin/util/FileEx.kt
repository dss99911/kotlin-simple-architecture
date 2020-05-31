package kim.jeonghyeon.simplearchitecture.plugin.util

import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.com.intellij.openapi.vfs.StandardFileSystems
import org.jetbrains.kotlin.com.intellij.openapi.vfs.VirtualFileManager
import org.jetbrains.kotlin.com.intellij.psi.PsiManager
import org.jetbrains.kotlin.psi.KtFile
import java.io.File
import java.io.FileWriter

fun File.toKtFile(project: Project): KtFile {
    val fileSystem =
        VirtualFileManager.getInstance().getFileSystem(StandardFileSystems.FILE_PROTOCOL)
    val psiManager = PsiManager.getInstance(project)

    return (fileSystem.findFileByPath(absolutePath)
        ?: error("can't fine virtual file : $absolutePath"))
        .let { psiManager.findFile(it) ?: error("can't fine psi file : $absolutePath") }
        .let { KtFile(it.viewProvider, false) }
}

fun File.write(action: FileWriter.() -> Unit): File {
    if (!exists()) {
        parentFile.mkdirs()
        createNewFile()
    }
    val fileWriter = FileWriter(this)
    action(fileWriter)

    fileWriter.close()
    return this
}