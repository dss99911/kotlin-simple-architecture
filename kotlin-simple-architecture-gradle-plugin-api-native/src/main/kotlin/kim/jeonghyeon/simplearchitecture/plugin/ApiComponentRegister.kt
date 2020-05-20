package kim.jeonghyeon.simplearchitecture.plugin

import com.google.auto.service.AutoService
import kim.jeonghyeon.simplearchitecture.plugin.model.ClassElement
import kim.jeonghyeon.simplearchitecture.plugin.model.ClassElementFindListener
import kim.jeonghyeon.simplearchitecture.plugin.processor.ApiClassProcessor
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

    override fun registerProjectComponents(
        project: com.intellij.mock.MockProject,
        configuration: CompilerConfiguration
    ) {
        val processor = ApiClassProcessor(configuration[KEY_PLUGIN_OPTIONS]!!)
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
                //this function is invoked by each declaration(class, property, enum, enum entry, constructor, value parameter etc..
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