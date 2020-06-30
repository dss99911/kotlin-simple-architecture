package kim.jeonghyeon.simplearchitecture.plugin

import kim.jeonghyeon.simplearchitecture.plugin.buildscript.applyAndroid
import kim.jeonghyeon.simplearchitecture.plugin.model.addGeneratedSourceDirectory
import kim.jeonghyeon.simplearchitecture.plugin.model.getSourceDirectorySetAndNames
import kim.jeonghyeon.simplearchitecture.plugin.task.getDeleteGeneratedSourceTask
import kim.jeonghyeon.simplearchitecture.plugin.task.getGenerateLocalAddressTask
import kim.jeonghyeon.simplearchitecture.plugin.util.addDependency
import kim.jeonghyeon.simplearchitecture.plugin.util.dependsOnCompileTask
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

        project.extensions.create(
            "simpleArch",
            SimpleArchExtension::class.java
        )

        with(project) {
            applyAndroid()

            //todo this is not working properly if there is library module. and application module. and both add same dependency
//            addSimpleArchitectureDependency()

            afterEvaluate {//to perform after source set is initialized.
                getSourceDirectorySetAndNames().forEach {
                    it.addGeneratedSourceDirectory(project)
                }
                dependsOnCompileTask { getDeleteGeneratedSourceTask(it) }
                dependsOnCompileTask { getGenerateLocalAddressTask(it) }

            }
        }
    }
}

fun Project.addSimpleArchitectureDependency() {
    addDependency(DEPENDENCY_SIMPLE_ARCHITECTURE, DEPENDENCY_SIMPLE_ARCHITECTURE_JVM)
}