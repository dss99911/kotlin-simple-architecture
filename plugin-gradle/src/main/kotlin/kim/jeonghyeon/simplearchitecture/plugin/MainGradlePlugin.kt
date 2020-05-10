package kim.jeonghyeon.simplearchitecture.plugin

import org.gradle.api.Project

open class MainGradlePlugin : org.gradle.api.Plugin<Project> {
    override fun apply(project: Project) {
        System.setProperty(
            "kotlin.compiler.execution.strategy",
            "in-process"
        ) // Enabling kotlin compiler plugin
        //TODO HYUN [multi-platform2] : add each sourceset for each target
        //todo consider buildtype and flavor type?

        // project.plugins : [org.gradle.api.plugins.HelpTasksPlugin@7473fdc1, org.gradle.buildinit.plugins.BuildInitPlugin@68f702c3, org.gradle.buildinit.plugins.WrapperPlugin@3b5bd1e2, com.android.build.gradle.internal.plugins.VersionCheckPlugin@23c8eecc, com.android.build.gradle.api.AndroidBasePlugin@483e8281, org.gradle.language.base.plugins.LifecycleBasePlugin@188f705f, org.gradle.api.plugins.BasePlugin@7dbe666d, org.gradle.api.plugins.ReportingBasePlugin@19644998, org.gradle.api.plugins.JavaBasePlugin@25f2fabf, com.android.build.gradle.internal.plugins.LibraryPlugin@469c7bd6, com.android.build.gradle.LibraryPlugin@481ff066, org.jetbrains.kotlin.gradle.scripting.internal.ScriptingGradleSubplugin@738c5874, org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper@411963b8, org.jetbrains.kotlinx.serialization.gradle.SerializationGradleSubplugin@64fee286, org.gradle.kotlin.dsl.provider.plugins.KotlinScriptBasePlugin@53d5a2bf]
        // project.convention.plugins : {base, java}

        project.afterEvaluate {//to perform after source set is initialized.
            (getSourceSetOptions() + getNativeSourceSetOptions()).forEach {
                it.addGeneratedSourceDirectory(project)
            }
        }
    }
}