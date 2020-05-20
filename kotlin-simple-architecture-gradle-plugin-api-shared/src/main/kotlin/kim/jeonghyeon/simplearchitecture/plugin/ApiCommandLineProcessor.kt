package kim.jeonghyeon.simplearchitecture.plugin

import com.google.auto.service.AutoService
import com.google.gson.Gson
import kim.jeonghyeon.simplearchitecture.plugin.model.PluginOptions
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey
import java.util.*

@AutoService(CommandLineProcessor::class)
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
            optionName = OPTION_PLUGIN_OPTIONS, valueDescription = "PluginOptions",
            description = "Plugin Options"
        )
    )

    override fun processOption(
        option: AbstractCliOption,
        value: String,
        configuration: CompilerConfiguration
    ) = when (option.optionName) {
        OPTION_PLUGIN_OPTIONS -> configuration.put(
            KEY_PLUGIN_OPTIONS,
            Gson().fromJson(String(Base64.getDecoder().decode(value)), PluginOptions::class.java)
        )
        else -> error("Unexpected config option ${option.optionName}")
    }
}

const val OPTION_PLUGIN_OPTIONS = "pluginsOptions"
val KEY_PLUGIN_OPTIONS = CompilerConfigurationKey<PluginOptions>(OPTION_PLUGIN_OPTIONS)