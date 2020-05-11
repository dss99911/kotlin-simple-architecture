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

@AutoService(KotlinGradleSubplugin::class) // don't forget!
class ApiGradleSubplugin : KotlinGradleSubplugin<AbstractCompile> {

    override fun isApplicable(project: Project, task: AbstractCompile): Boolean =
        project.plugins.hasPlugin(MainGradlePlugin::class.java)

    /**
     * Just needs to be consistent with the key for CommandLineProcessor#pluginId
     */
    override fun getCompilerPluginId(): String = "SimpleApiPlugin"

    //todo check if it's possible to migrate to use getSubpluginKotlinTasks instead of new module.
    override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact(
        groupId = "kim.jeonghyeon",
        artifactId = "kotlin-simple-architecture-gradle-plugin-api",
        version = "1.0.2" // remember to bump this version before any release!
    )

    //use same artifact because we don't change compile code. but just creating kt file.
    override fun getNativeCompilerPluginArtifact(): SubpluginArtifact = SubpluginArtifact(
        groupId = "kim.jeonghyeon",
        artifactId = "kotlin-simple-architecture-gradle-plugin-api-native",
        version = "1.0.2" // remember to bump this version before any release!
    )

    //called 23 times. why is this called a lot?
    override fun apply(
        project: Project,
        kotlinCompile: AbstractCompile,
        javaCompile: AbstractCompile?,
        variantData: Any?,
        androidProjectHandler: Any?,
        kotlinCompilation: KotlinCompilation<KotlinCommonOptions>?
    ): List<SubpluginOption> {
        val result = mutableListOf<SubpluginOption>()
        project.afterEvaluate {
            val options = project.getSourceSetOptions().map { it.toOption() }

            //doesn't allow some special character. so, used Base64
            val sourceSetsString =
                Base64.getEncoder().encodeToString(Gson().toJson(options).toByteArray())

            println("sub source : " + options.joinToString { it.name })

            result.addAll(
                listOf(
                    SubpluginOption("sourceSets", sourceSetsString),
                    SubpluginOption(key = "buildPath", value = project.buildDir.toString())
                )
            )
        }
        return result
    }
}
