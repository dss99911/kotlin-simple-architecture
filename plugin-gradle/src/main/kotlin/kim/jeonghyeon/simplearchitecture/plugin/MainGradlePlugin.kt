package kim.jeonghyeon.simplearchitecture.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

open class MainGradlePlugin : Plugin<Project> {
    //this is called 1 time on configuration step.
    override fun apply(project: Project) {
        System.setProperty(
            "kotlin.compiler.execution.strategy",
            "in-process"
        ) // Enabling kotlin compiler plugin

        project.afterEvaluate {//to perform after source set is initialized.
            getSourceSetOptions().forEach {
                it.addGeneratedSourceDirectory(project)
            }
            applyGeneratedCodeDeleteTask()
        }


    }
}