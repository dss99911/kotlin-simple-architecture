package kim.jeonghyeon.simplearchitecture.plugin

import com.google.auto.service.AutoService
import de.jensklingenberg.mpapt.common.MpAptProject
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.extensions.StorageComponentContainerContributor

@AutoService(ComponentRegistrar::class)
class NativeApiComponentRegistrar : ComponentRegistrar {

    override fun registerProjectComponents(
        project: com.intellij.mock.MockProject,
        configuration: CompilerConfiguration
    ) {
        val processor =
            ApiClassProcessor(configuration[KEY_BUILD_PATH]!!, configuration[KEY_SOURCE_SET]!!)
        val mpapt = MpAptProject(processor, configuration)
        //it doesn't change compiled class. but just create kt file.
        //todo is it available to use embedible library?
        StorageComponentContainerContributor.registerExtension(project, mpapt)
    }
}