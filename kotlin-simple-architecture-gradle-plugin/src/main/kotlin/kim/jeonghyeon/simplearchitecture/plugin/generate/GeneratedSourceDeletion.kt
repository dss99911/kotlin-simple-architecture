package kim.jeonghyeon.simplearchitecture.plugin.generate

import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.tasks.factory.dependsOn
import kim.jeonghyeon.simplearchitecture.plugin.model.generatedSourceSetPath
import kim.jeonghyeon.simplearchitecture.plugin.util.androidExtension
import kim.jeonghyeon.simplearchitecture.plugin.util.multiplatformExtension
import org.gradle.api.DomainObjectSet
import org.gradle.api.Project
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.TaskProvider
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import java.io.File

fun Project.registerDeleteTasks() {
    multiplatformExtension?.registerDeleteTasks(this)
        ?: androidExtension?.registerDeleteTasks(this)
        ?: registerDeleteTaskOfJvm()
}

private fun KotlinMultiplatformExtension.registerDeleteTasks(project: Project) {
    targets
        .flatMap { it.compilations }
        .filter { !it.name.endsWith(suffix = "Test", ignoreCase = true) }
        .forEach {
            it.compileKotlinTask.dependsOn(project.getDeleteTask("${it.target.name}${it.name.capitalize()}"))
        }
}

private fun BaseExtension.registerDeleteTasks(project: Project) {
    val variants: DomainObjectSet<out BaseVariant> = when (this) {
        is AppExtension -> applicationVariants
        is LibraryExtension -> libraryVariants
        else -> throw IllegalStateException("Unknown Android plugin $this")
    }

    variants.forEach { variant ->
        project.tasks.named("compile${variant.name.capitalize()}Kotlin")
            .dependsOn(project.getDeleteTask(variant.name))
    }
}

private fun Project.registerDeleteTaskOfJvm() {
    tasks.named("compileKotlin").dependsOn(getDeleteTask("main"))
}


fun Project.getDeleteTask(compileSourceSetName: String): TaskProvider<Delete> =
    tasks.register("delete${compileSourceSetName.capitalize()}ApiImpl", Delete::class.java) {
        delete(File(generatedSourceSetPath(project.buildDir.toString(), compileSourceSetName)))
        //todo consider not to delete if source is up-to-date
    }