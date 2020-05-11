package kim.jeonghyeon.simplearchitecture.plugin

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.com.intellij.mock.MockProject
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
class ApiComponentRegistrar : ComponentRegistrar {

    //this function is called 2times. I don't know why
    override fun registerProjectComponents(
        project: MockProject,
        configuration: CompilerConfiguration
    ) {
        println("jvm : " + configuration[KEY_SOURCE_SET]!!.joinToString { it.name })
        val processor =
            ApiClassProcessor(configuration[KEY_BUILD_PATH]!!, configuration[KEY_SOURCE_SET]!!)
        //it doesn't change compiled class. but just create kt file.
        //todo is it available to use embedible library?
        StorageComponentContainerContributor.registerExtension(
            project,
            ClassElementFinder(processor)
        )
    }
}

class ClassElementFinder(val listener: ClassElementFindListener) :
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