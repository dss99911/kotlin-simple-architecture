package kim.jeonghyeon.simplearchitecture.plugin.config

import kim.jeonghyeon.simplearchitecture.plugin.extension.simpleArchExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun Project.applyCommonConfig() {
    afterEvaluate {
        if (!simpleArchExtension.commonConfig) {
            return@afterEvaluate
        }

        System.setProperty(// Enabling kotlin compiler plugin
            "kotlin.compiler.execution.strategy",
            "in-process"
        )

        project.tasks.withType<KotlinCompile> {
            kotlinOptions.jvmTarget = "1.8"
        }
    }

}