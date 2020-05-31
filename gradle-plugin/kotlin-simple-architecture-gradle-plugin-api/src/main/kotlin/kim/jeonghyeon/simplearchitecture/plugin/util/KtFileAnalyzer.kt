package kim.jeonghyeon.simplearchitecture.plugin.util

import kim.jeonghyeon.simplearchitecture.plugin.generator.INDENT
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.impl.PsiElementBase
import org.jetbrains.kotlin.psi.*

class KtFileAnalyzer(val files: Collection<KtFile>) {

    /**
     * @param fileName : include '.kt'
     */
    fun analyze(fileName: String) {
        files.filter { it.name == fileName }
            .forEach { analyze(it) }
    }

    fun analyze(file: KtFile) {
        log("=== ${file.packageFqName.asString()}.${file.name} Detail ===")
        log("importDirectives=${file.importDirectives.map { it.text }}")
        log("importList=${file.importList?.text}", "")
        log("virtualFilePath=${file.virtualFilePath}")
        log("declarations=${file.declarations.map { it.text }}")


        file.children.forEach { it.printElement(INDENT) }
    }


    fun PsiElement.printElement(indent: String) {
        val logElement: (String) -> Unit = { log(it, indent) }

        logElement("=== ${this::class.qualifiedName} Detail ===")
        logElement("text=${text}")
        if (this is PsiElementBase) {
            logElement("name=${name}")
        }


        when (this) {
            is KtDeclarationModifierList -> {
                logElement("annotations=${annotations.map { it.text }}")
            }
            is KtClass -> {
                logElement("isInterface=${this.isInterface()}")
                logElement("keyword=${getClassOrInterfaceKeyword()?.text}")
                logElement("annotations=${this.annotationEntries.map { it.name }}")
                logElement("declarations=${declarations.map { it::class.qualifiedName }}")
            }

            is KtImportList -> {
                logElement("imports=")
                imports.forEach { it.printElement(indent + INDENT) }
            }

            is KtImportDirective -> {
                logElement("alias=${alias?.text}")
                logElement("importPath.path=${importPath?.pathStr}")
                logElement("importPath.name=${importPath?.fqName?.asString()}")
                logElement("importPath.alias=${importPath?.alias?.asString()}")
                logElement("importPath.importedName=${importPath?.importedName?.asString()}}")
            }

            is KtNamedFunction -> {
                logElement("modifiers=${modifierList}")

                logElement("parameters=")
                parameters.forEach { it.printElement(indent + INDENT) }
                logElement("reference=")
                typeReference?.typeElement?.printElement(indent + INDENT)
                logElement("keyword=${funKeyword?.text}")
                logElement("hasReturn=${hasDeclaredReturnType()}")
                logElement("colon=${colon?.text}")
            }

            is KtTypeReference -> {
                logElement("annotations=")
                this.annotations.forEach { it.printElement(indent + INDENT) }
                logElement("reference=${this.reference?.canonicalText}")
                logElement("references=${this.references.map { it.canonicalText }}")
                logElement("typeElement?=")
                typeElement?.printElement(indent + INDENT)

            }
        }

        logElement("children=")
        this.children.forEach { it.printElement(indent + INDENT) }
    }
}

fun log(text: String, indent: String = "") {
    println(text.prependIndent("KtFileAnalyzer : $indent"))
}