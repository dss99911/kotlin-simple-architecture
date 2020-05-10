package kim.jeonghyeon.simplearchitecture.plugin

import com.google.auto.service.AutoService
import org.gradle.internal.impldep.com.google.gson.Gson
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey

@AutoService(CommandLineProcessor::class) // don't forget!
class ApiCommandLineProcessor : CommandLineProcessor {
    /**
     * Just needs to be consistent with the key for GradleSubplugin#getCompilerPluginId
     */
    override val pluginId: String = "SimpleApiPlugin"

    /**
     * Should match up with the options we return from our GradleSubplugin.
     * Should also have matching when branches for each name in the [processOption] function below
     */
    override val pluginOptions: Collection<CliOption> = listOf(
        CliOption(
            optionName = "buildPath", valueDescription = "String",
            description = "build path"
        ),
        CliOption(
            optionName = "sourceSets", valueDescription = "SourceSetOption",
            description = "source sets",
            required = true, allowMultipleOccurrences = true
        )
    )

    override fun processOption(
        option: AbstractCliOption,
        value: String,
        configuration: CompilerConfiguration
    ) = when (option.optionName) {
        "buildPath" -> configuration.put(KEY_BUILD_PATH, value)
        "sourceSets" -> configuration.appendList(
            KEY_SOURCE_SET,
            Gson().fromJson(value, SourceSetOption::class.java)
        )
        else -> error("Unexpected config option ${option.optionName}")
    }
}

val KEY_BUILD_PATH = CompilerConfigurationKey<String>("build path")
val KEY_SOURCE_SET = CompilerConfigurationKey<List<SourceSetOption>>("source set")
