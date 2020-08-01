package kim.jeonghyeon.simplearchitecture.plugin.config

import kim.jeonghyeon.simplearchitecture.plugin.extension.simpleArchExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun Project.applyCommonConfig() {
    if (!simpleArchExtension.commonConfig) {
        return
    }

    System.setProperty(// Enabling kotlin compiler plugin
        "kotlin.compiler.execution.strategy",
        "in-process"
    )

    project.tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    apply(plugin = "com.squareup.sqldelight")
    apply(plugin = "kotlinx-serialization")

}