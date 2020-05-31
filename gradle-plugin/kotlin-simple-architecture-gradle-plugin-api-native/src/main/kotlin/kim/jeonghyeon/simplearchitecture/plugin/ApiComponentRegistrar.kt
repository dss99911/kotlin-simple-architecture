package kim.jeonghyeon.simplearchitecture.plugin

import com.google.auto.service.AutoService
import com.intellij.mock.MockProject
import com.intellij.openapi.project.Project
import kim.jeonghyeon.simplearchitecture.plugin.generator.ApiGenerator
import kim.jeonghyeon.simplearchitecture.plugin.generator.DbGenerator
import kim.jeonghyeon.simplearchitecture.plugin.util.toKtFile
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.extensions.CollectAdditionalSourcesExtension
import org.jetbrains.kotlin.extensions.StorageComponentContainerContributor
import org.jetbrains.kotlin.psi.KtFile

/**
 * 1st (in case kapt is used): compile -> CollectAdditionalSourcesExtension -> ClassBuilderInterceptorExtension -> kapt task
 * 2nd compile: compile -> CollectAdditionalSourcesExtension -> StorageComponentContainerContributor -> ClassBuilderInterceptorExtension
 */
@AutoService(ComponentRegistrar::class)
class ApiComponentRegistrar : ComponentRegistrar {
    /**
     * this is called by compile task
     * target + variants(flavors, build type)
     * this is called two times a compile task.
     * [StorageComponentContainerContributor] is not working on first call. I don't know what is the difference
     */
    override fun registerProjectComponents(
        project: MockProject,
        configuration: CompilerConfiguration
    ) {
        /**
         * I tried to generate file on [StorageComponentContainerContributor].
         * but It was not proper approach.
         * because, [StorageComponentContainerContributor] is called while compile source file.
         * so, the generated file is not recognized.
         */
        CollectAdditionalSourcesExtension.registerExtension(
            project,
            object : CollectAdditionalSourcesExtension {
                override fun collectAdditionalSourcesAndUpdateConfiguration(
                    knownSources: Collection<KtFile>,
                    configuration: CompilerConfiguration,
                    project: Project
                ): Collection<KtFile> {
                    val pluginOptions = configuration[KEY_PLUGIN_OPTIONS]!!
                    val apiFiles = ApiGenerator(pluginOptions, knownSources).generate()
                    val dbFiles = DbGenerator(pluginOptions, knownSources).generate()

                    return (apiFiles + dbFiles).map { it.toKtFile(project) }
                }
            })
    }
}