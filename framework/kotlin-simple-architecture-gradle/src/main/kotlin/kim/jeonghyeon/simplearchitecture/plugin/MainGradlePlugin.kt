package kim.jeonghyeon.simplearchitecture.plugin

import kim.jeonghyeon.simplearchitecture.plugin.config.applyAndroidConfig
import kim.jeonghyeon.simplearchitecture.plugin.config.applyCommonConfig
import kim.jeonghyeon.simplearchitecture.plugin.config.applyGenerationConfig
import kim.jeonghyeon.simplearchitecture.plugin.config.applySimpleConfig
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

open class MainGradlePlugin : Plugin<Project> {
    //this is called 1 time on configuration step.
    override fun apply(project: Project) = with(project) {
        apply(plugin = "com.squareup.sqldelight")
        apply(plugin = "kotlinx-serialization")

        applyCommonConfig()
        applySimpleConfig()
        applyAndroidConfig()
        applyGenerationConfig()
    }
}
