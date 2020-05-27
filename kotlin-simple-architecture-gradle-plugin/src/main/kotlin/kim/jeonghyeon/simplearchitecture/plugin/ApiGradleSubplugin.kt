package kim.jeonghyeon.simplearchitecture.plugin

import com.google.auto.service.AutoService
import com.google.gson.Gson
import kim.jeonghyeon.simplearchitecture.plugin.generate.getSourceSetOptions
import kim.jeonghyeon.simplearchitecture.plugin.generate.toOption
import kim.jeonghyeon.simplearchitecture.plugin.model.PluginOptions
import org.gradle.api.Project
import org.gradle.api.tasks.compile.AbstractCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonOptions
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinGradleSubplugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption
import java.util.*

@AutoService(KotlinGradleSubplugin::class)
class ApiGradleSubplugin : KotlinGradleSubplugin<AbstractCompile> {

    override fun isApplicable(project: Project, task: AbstractCompile): Boolean =
        project.plugins.hasPlugin(MainGradlePlugin::class.java)

    /**
     * Just needs to be consistent with the key for CommandLineProcessor#pluginId
     */
    override fun getCompilerPluginId(): String = "SimpleApiPlugin"

    override fun getPluginArtifact(): SubpluginArtifact =
        DEPENDENCY_SIMPLE_ARCHITECTURE_PLUGIN_API.split(":").let {
            SubpluginArtifact(
                groupId = it[0],
                artifactId = it[1],
                version = it[2]
            )
        }

    override fun getNativeCompilerPluginArtifact(): SubpluginArtifact =
        DEPENDENCY_SIMPLE_ARCHITECTURE_PLUGIN_API_NATIVE.split(":").let {
            SubpluginArtifact(
                groupId = it[0],
                artifactId = it[1],
                version = it[2]
            )
        }

    override fun apply(
        project: Project,
        kotlinCompile: AbstractCompile,
        javaCompile: AbstractCompile?,
        variantData: Any?,
        androidProjectHandler: Any?,
        kotlinCompilation: KotlinCompilation<KotlinCommonOptions>?
    ): List<SubpluginOption> {

        return PluginOptions(
            project.getSourceSetOptions().map { it.toOption() },
            project.buildDir.toString(),
            kotlinCompile.targetVariantsName
        )
            //it doesn't allow some special character. so, used Base64
            .let { Base64.getEncoder().encodeToString(Gson().toJson(it).toByteArray()) }
            .let { listOf(SubpluginOption(OPTION_PLUGIN_OPTIONS, it)) }
    }

    private val AbstractCompile.targetVariantsName
        get(): String {
            val variants = toString().substringAfter(":compile").substringBefore("Kotlin")
                .let { if (it.isEmpty()) "Main" else it }
            val target = toString().substringAfterLast("Kotlin").substringBefore("'")
            return target.decapitalize() + variants
    }
}
