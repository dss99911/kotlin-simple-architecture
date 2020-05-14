package kim.jeonghyeon.simplearchitecture.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

open class MainGradlePlugin : Plugin<Project> {
    //this is called 1 time on configuration step.
    override fun apply(project: Project) {
        System.setProperty(// Enabling kotlin compiler plugin
            "kotlin.compiler.execution.strategy",
            "in-process"
        )

        with(project) {
            applyAndroid()
            applySourceGeneration()
        }
    }
}