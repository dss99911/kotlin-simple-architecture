package kim.jeonghyeon.simplearchitecture.plugin.task

import kim.jeonghyeon.simplearchitecture.plugin.model.generatedSourceSetPath
import org.gradle.api.Project
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.TaskProvider
import java.io.File

fun Project.getDeleteGeneratedSourceTask(compileSourceSetName: String): TaskProvider<Delete> =
    tasks.register("delete${compileSourceSetName.capitalize()}ApiImpl", Delete::class.java) {
        delete(File(generatedSourceSetPath(project.buildDir.toString(), compileSourceSetName)))
        //todo consider not to delete if source is up-to-date
    }