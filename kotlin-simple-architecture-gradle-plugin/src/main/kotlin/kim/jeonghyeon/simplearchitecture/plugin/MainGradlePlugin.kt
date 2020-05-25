package kim.jeonghyeon.simplearchitecture.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

open class MainGradlePlugin : Plugin<Project> {
    //this is called 1 time on configuration step.
    override fun apply(project: Project) {
        System.setProperty(// Enabling kotlin compiler plugin
            "kotlin.compiler.execution.strategy",
            "in-process"
        )

        project.tasks.withType<KotlinCompile> {
            kotlinOptions.jvmTarget = "1.8"
        }

        with(project) {
            applyAndroid()
            addGeneratedSourceDirectories()
            addSimpleArchitectureDependency()
        }
    }
}

fun Project.addSimpleArchitectureDependency() {
    addDependency(DEPENDENCY_SIMPLE_ARCHITECTURE, DEPENDENCY_SIMPLE_ARCHITECTURE_JVM)
}