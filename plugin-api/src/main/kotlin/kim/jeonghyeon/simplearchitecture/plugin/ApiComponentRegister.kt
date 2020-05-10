package kim.jeonghyeon.simplearchitecture.plugin

import com.google.auto.service.AutoService
import de.jensklingenberg.mpapt.common.MpAptProject
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.extensions.StorageComponentContainerContributor

@AutoService(ComponentRegistrar::class)
class ApiComponentRegistrar : ComponentRegistrar {

    override fun registerProjectComponents(
        project: MockProject,
        configuration: CompilerConfiguration
    ) {
        val processor =
            ApiClassProcessor(configuration[KEY_BUILD_PATH]!!, configuration[KEY_SOURCE_SET]!!)
        val mpapt = MpAptProject(processor, configuration)
        //it doesn't change compiled class. but just create kt file.
        StorageComponentContainerContributor.registerExtension(project, mpapt)
    }
}