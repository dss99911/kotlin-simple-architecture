package kim.jeonghyeon.simplearchitecture.plugin

import com.google.auto.service.AutoService
import kim.jeonghyeon.simplearchitecture.plugin.model.ClassElement
import kim.jeonghyeon.simplearchitecture.plugin.model.ClassElementRetrievalListener
import kim.jeonghyeon.simplearchitecture.plugin.processor.ApiClassProcessor
import org.jetbrains.kotlin.codegen.extensions.ClassBuilderInterceptorExtension
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.container.StorageComponentContainer
import org.jetbrains.kotlin.container.useInstance
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.extensions.StorageComponentContainerContributor
import org.jetbrains.kotlin.js.translate.extensions.JsSyntheticTranslateExtension
import org.jetbrains.kotlin.platform.TargetPlatform
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.resolve.checkers.DeclarationChecker
import org.jetbrains.kotlin.resolve.checkers.DeclarationCheckerContext
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe

@AutoService(ComponentRegistrar::class)
class ApiComponentRegistrar : ComponentRegistrar {
    //this is called by compile task
    //when build, this is called several times.
    //target + variants(flavors, build type)

    override fun registerProjectComponents(
        project: MockProject,
        configuration: CompilerConfiguration
    ) {
        val processor = ApiClassProcessor(configuration[KEY_PLUGIN_OPTIONS]!!)

        StorageComponentContainerContributor.registerExtension(
            project,
            ClassElementFinder(processor)
        )
        ClassBuilderInterceptorExtension.registerExtension(
            project,
            ClassElementRetrievalFinishDetector(processor)
        )
        JsSyntheticTranslateExtension.registerExtension(
            project,
            ClassElementRetrievalFinishDetector(processor)
        )
    }
}

class ClassElementFinder(val listener: ClassElementRetrievalListener) :
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
                        declaration.containingFile.virtualFile.canonicalPath!!,
                        descriptor.original.containingDeclaration.fqNameSafe.asString(),
                        descriptor
                    )
                )
            }
        })
    }
}