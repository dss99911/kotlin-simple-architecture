package kim.jeonghyeon.simplearchitecture.plugin

import com.android.build.gradle.BaseExtension
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.sources.DefaultKotlinSourceSet

val Project.androidExtension get() = project.extensions.findByType(BaseExtension::class.java)
val Project.hasAndroid get() = androidExtension != null
val Project.isMultiplatform get() = plugins.hasPlugin("org.jetbrains.kotlin.multiplatform")
val Project.multiplatformExtension get() = project.extensions.findByType(KotlinMultiplatformExtension::class.java)

fun Project.addDependency(multiplatformDependency: String, jvmDependency: String) {
    if (isMultiplatform) {
        val sourceSets = multiplatformExtension!!.sourceSets
        val sourceSet = (sourceSets.findByName("commonMain") as? DefaultKotlinSourceSet?)?: return
        configurations.getByName(sourceSet.apiConfigurationName).dependencies.add(
            dependencies.create(multiplatformDependency)
        )
    } else {
        configurations.getByName("api").dependencies.add(
            dependencies.create(jvmDependency)
        )


    }
}