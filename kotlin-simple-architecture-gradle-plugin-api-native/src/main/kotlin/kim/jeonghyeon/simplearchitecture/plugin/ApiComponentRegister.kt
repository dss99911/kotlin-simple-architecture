package kim.jeonghyeon.simplearchitecture.plugin

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.container.StorageComponentContainer
import org.jetbrains.kotlin.container.useInstance
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.extensions.StorageComponentContainerContributor
import org.jetbrains.kotlin.platform.TargetPlatform
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.resolve.checkers.DeclarationChecker
import org.jetbrains.kotlin.resolve.checkers.DeclarationCheckerContext
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe

@AutoService(ComponentRegistrar::class)
class NativeApiComponentRegistrar : ComponentRegistrar {

    //this instance is created and called several times.
    override fun registerProjectComponents(
        project: com.intellij.mock.MockProject,
        configuration: CompilerConfiguration
    ) {

        val processor = ApiClassProcessor(
            configuration[KEY_BUILD_PATH]!!,
            configuration[KEY_SOURCE_SET]!!,
            true
        )
        StorageComponentContainerContributor.registerExtension(
            project,
            ClassElementFinder(processor)
        )
    }
}

class ClassElementFinder(
    val listener: ClassElementFindListener
) :
    StorageComponentContainerContributor {
    override fun registerModuleComponents(
        container: StorageComponentContainer,
        platform: TargetPlatform,
        moduleDescriptor: org.jetbrains.kotlin.descriptors.ModuleDescriptor
    ) {
        container.useInstance(object : DeclarationChecker {
            override fun check(
                declaration: KtDeclaration,
                descriptor: DeclarationDescriptor,
                context: DeclarationCheckerContext
            ) {
                //this is invoked by each declaration(class, property, enum, enum entry, constructor, value parameter etc..
                if (descriptor !is ClassDescriptor) return

                listener.onClassElementFound(
                    ClassElement(
                        descriptor.name.asString(),
                        declaration.containingFile.virtualFile.canonicalPath!!,//this data can not be fetched on shared plugin module. because shared plugin use embeddable dependency.
                        descriptor.original.containingDeclaration.fqNameSafe.asString(),
                        descriptor
                    )
                )
            }
        })
    }
}

/**
 * In the Kotlin Native Compiler the configuration map has an entry with the name "target we compile for"
 * the value is one of [de.jensklingenberg.mpapt.utils.KonanTargetValues]. I don't know how to get the value
 * out of the configuration map other than parse the "toString()". This function will return an empty value
 * when it's used on Kotlin JVM/JS Compiler because the CompilerConfiguration doesn't have that value.
 */
fun CompilerConfiguration.nativeTargetPlatformName(): String {
    val targetKeyword = "target we compile for="
    val mapString = this.toString()
    return if (!mapString.contains(targetKeyword)) {
        ""
    } else {
        this.toString().substringAfter(targetKeyword).substringBefore(",")
    }
}