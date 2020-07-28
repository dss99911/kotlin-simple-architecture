package kim.jeonghyeon.simplearchitecture.plugin.config

import kim.jeonghyeon.simplearchitecture.plugin.extension.simpleArchExtension
import kim.jeonghyeon.simplearchitecture.plugin.model.addGeneratedSourceDirectory
import kim.jeonghyeon.simplearchitecture.plugin.model.getSourceDirectorySetAndNames
import kim.jeonghyeon.simplearchitecture.plugin.task.getDeleteGeneratedSourceTask
import kim.jeonghyeon.simplearchitecture.plugin.util.dependsOnCompileTask
import org.gradle.api.Project

fun Project.applyGenerationConfig() {
    afterEvaluate {//to perform after source set is initialized.
        if (!simpleArchExtension.generationConfig) {
            return@afterEvaluate
        }

        getSourceDirectorySetAndNames().forEach {
            it.addGeneratedSourceDirectory(project)
        }
        dependsOnCompileTask { getDeleteGeneratedSourceTask(it) }

    }
}