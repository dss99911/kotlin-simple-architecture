package kim.jeonghyeon.simplearchitecture.plugin

import com.google.auto.service.AutoService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
            optionName = OPTION_BUILD_PATH, valueDescription = "String",
            description = "build path"
        ),
        CliOption(
            optionName = OPTION_PROJECT_PATH, valueDescription = "String",
            description = "project path"
        ),
        CliOption(
            optionName = OPTION_SOURCE_SETS, valueDescription = "SourceSetOption",
            description = "source sets"
        )
    )

    override fun processOption(
        option: AbstractCliOption,
        value: String,
        configuration: CompilerConfiguration
    ) = when (option.optionName) {
        OPTION_BUILD_PATH -> configuration.put(KEY_BUILD_PATH, value)
        OPTION_PROJECT_PATH -> configuration.put(KEY_PROJECT_PATH, value)
        OPTION_SOURCE_SETS -> configuration.put(
            KEY_SOURCE_SET,
            Gson().fromJson(
                String(Base64.getDecoder().decode(value)),
                object : TypeToken<ArrayList<SourceSetOption>>() {}.type
            )
        )
        else -> error("Unexpected config option ${option.optionName}")
    }
}

const val OPTION_BUILD_PATH = "buildPath"
const val OPTION_PROJECT_PATH = "projectPath"
const val OPTION_SOURCE_SETS = "sourceSets"
val KEY_BUILD_PATH = CompilerConfigurationKey<String>(OPTION_BUILD_PATH)
val KEY_PROJECT_PATH = CompilerConfigurationKey<String>(OPTION_PROJECT_PATH)
val KEY_SOURCE_SET = CompilerConfigurationKey<List<SourceSetOption>>(OPTION_SOURCE_SETS)