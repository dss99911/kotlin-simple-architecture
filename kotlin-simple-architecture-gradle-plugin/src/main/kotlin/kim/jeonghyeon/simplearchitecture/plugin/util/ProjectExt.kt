package kim.jeonghyeon.simplearchitecture.plugin.util

import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.tasks.factory.dependsOn
import org.gradle.api.DomainObjectSet
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.sources.DefaultKotlinSourceSet

val Project.androidExtension get() = project.extensions.findByType(BaseExtension::class.java)
val Project.hasAndroid get() = androidExtension != null
val Project.isMultiplatform get() = plugins.hasPlugin("org.jetbrains.kotlin.multiplatform")
val Project.multiplatformExtension get() = project.extensions.findByType(KotlinMultiplatformExtension::class.java)

fun Project.addDependency(multiplatformDependency: String, jvmDependency: String) {
    if (isMultiplatform) {
        val sourceSets = multiplatformExtension!!.sourceSets
        val sourceSet = (sourceSets.findByName("commonMain") as? DefaultKotlinSourceSet?) ?: return
        configurations.getByName(sourceSet.apiConfigurationName).dependencies.add(
            dependencies.create(multiplatformDependency)
        )
    } else {
        configurations.getByName("api").dependencies.add(
            dependencies.create(jvmDependency)
        )
    }
}

fun Project.dependsOnCompileTask(task: (defaultSourceSetName: String) -> TaskProvider<out Task>) {
    multiplatformExtension?.dependsOnCompileTask(task)
        ?: androidExtension?.dependsOnCompileTask(this, task)
        ?: dependsOnCompileTaskOfJvm(task)
}

private fun KotlinMultiplatformExtension.dependsOnCompileTask(
    task: (defaultSourceSetName: String) -> TaskProvider<out Task>
) {
    targets
        .flatMap { it.compilations }
        .filter { !it.name.endsWith(suffix = "Test", ignoreCase = true) }
        .forEach {
            it.compileKotlinTask.dependsOn(task("${it.target.name}${it.name.capitalize()}"))
        }
}

private fun BaseExtension.dependsOnCompileTask(
    project: Project,
    task: (defaultSourceSetName: String) -> TaskProvider<out Task>
) {
    val variants: DomainObjectSet<out BaseVariant> = when (this) {
        is AppExtension -> applicationVariants
        is LibraryExtension -> libraryVariants
        else -> throw IllegalStateException("Unknown Android plugin $this")
    }

    variants.forEach { variant ->
        project.tasks.named("compile${variant.name.capitalize()}Kotlin")
            .dependsOn(task(variant.name))
    }
}

private fun Project.dependsOnCompileTaskOfJvm(task: (defaultSourceSetName: String) -> TaskProvider<out Task>) {
    tasks.named("compileKotlin").dependsOn(task("main"))
}
