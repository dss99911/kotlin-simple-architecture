package kim.jeonghyeon.simplearchitecture.plugin.util

import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.BaseVariant
import org.gradle.api.DomainObjectSet
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
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
    getCompileInfos().forEach {
        it.task.dependsOn(task(it.targetVariantsName))
    }
}

fun Project.getCompileInfos() =
    multiplatformExtension?.dependsOnCompileTask()
    ?: androidExtension?.dependsOnCompileTask(this)
    ?: dependsOnCompileTaskOfJvm()

private fun KotlinMultiplatformExtension.dependsOnCompileTask() = targets.flatMap { target ->
    target.compilations
        .filter { !target.name.endsWith(suffix = "Test", ignoreCase = true) }
        .map {
            CompileInfo(it.compileKotlinTask, "${it.target.name}${it.name.capitalize()}", target.platformType)
        }
}

private fun BaseExtension.dependsOnCompileTask(
    project: Project
): List<CompileInfo> {
    val variants: DomainObjectSet<out BaseVariant> = when (this) {
        is AppExtension -> applicationVariants
        is LibraryExtension -> libraryVariants
        else -> throw IllegalStateException("Unknown Android plugin $this")
    }

    return variants.map { variant ->
        CompileInfo(
            project.tasks.named("compile${variant.name.capitalize()}Kotlin").get(),
            variant.name,
            KotlinPlatformType.androidJvm
        )
    }
}

private fun Project.dependsOnCompileTaskOfJvm() =
    listOf(CompileInfo(tasks.named("compileKotlin").get(), "main", KotlinPlatformType.jvm))


data class CompileInfo(val task: Task, val targetVariantsName: String, val platformType: KotlinPlatformType)