package kim.jeonghyeon.simplearchitecture.plugin

import com.google.auto.service.AutoService
import com.google.gson.Gson
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

    override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact(
        groupId = "kim.jeonghyeon",
        artifactId = "kotlin-simple-architecture-gradle-plugin-api",
        version = "1.0.2"
    )

    override fun getNativeCompilerPluginArtifact(): SubpluginArtifact = SubpluginArtifact(
        groupId = "kim.jeonghyeon",
        artifactId = "kotlin-simple-architecture-gradle-plugin-api-native",
        version = "1.0.2"
    )

    override fun apply(
        project: Project,
        kotlinCompile: AbstractCompile,
        javaCompile: AbstractCompile?,
        variantData: Any?,
        androidProjectHandler: Any?,
        kotlinCompilation: KotlinCompilation<KotlinCommonOptions>?
    ): List<SubpluginOption> {
        val options = project.getSourceSetOptions().map { it.toOption() }

        //it doesn't allow some special character. so, used Base64
        val sourceSetsString =
            Base64.getEncoder().encodeToString(Gson().toJson(options).toByteArray())

        return listOf(
            SubpluginOption(OPTION_SOURCE_SETS, sourceSetsString),
            SubpluginOption(OPTION_BUILD_PATH, project.buildDir.toString())
        )
    }

}
