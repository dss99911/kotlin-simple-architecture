package kim.jeonghyeon.simplearchitecture.plugin

import kim.jeonghyeon.simplearchitecture.plugin.config.applyAndroidConfig
import kim.jeonghyeon.simplearchitecture.plugin.config.applyCommonConfig
import kim.jeonghyeon.simplearchitecture.plugin.config.applyGenerationConfig
import kim.jeonghyeon.simplearchitecture.plugin.config.applySimpleConfig
import kim.jeonghyeon.simplearchitecture.plugin.extension.SimpleArchExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

open class MainGradlePlugin : Plugin<Project> {
    //this is called 1 time on configuration step.
    override fun apply(project: Project) = with(project) {
        extensions.create("simpleArch", SimpleArchExtension::class.java)

        applyCommonConfig()
        applySimpleConfig()
        applyAndroidConfig()
        applyGenerationConfig()
    }
}