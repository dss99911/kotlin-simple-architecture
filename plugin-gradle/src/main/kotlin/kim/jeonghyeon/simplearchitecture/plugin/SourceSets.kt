package kim.jeonghyeon.simplearchitecture.plugin


import com.android.build.gradle.BaseExtension
import com.android.build.gradle.api.AndroidSourceSet
import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.HasConvention
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KOTLIN_DSL_NAME
import org.jetbrains.kotlin.gradle.plugin.KOTLIN_JS_DSL_NAME
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

const val NATIVE_TARGET_NAME = "native"

fun Project.getSourceSetOptions(): List<SourceDirectorySetAndName> {
    //TODO HYUN [multi-platform2] : test on multiplatform, android, kotlin project

    // Multiplatform project.
    project.extensions.findByType(KotlinMultiplatformExtension::class.java)?.let {
        println("multiplatform sources")
        println("sourceset : " + it.sourceSets.map { "${it.name}, ${it.kotlin.sourceDirectories.asPath}" })

        return it.sourceSets
            .filter {
                !it.name.contains(
                    "test",
                    true
                )
            }//ex) androidTest, androidTestDebug. androidTestRelease
            .map { SourceDirectorySetAndName(it.name, it.kotlin) }
    }

    // Android project.
    project.extensions.findByType(BaseExtension::class.java)?.let {
        println("android sources")
        return it.sourceSets
            .filter {
                !it.name.contains(
                    "test",
                    true
                )
            }//ex) androidTest, androidTestDebug. androidTestRelease
            .map { SourceDirectorySetAndName(it.name, it.kotlin!!) }
    }

    // Kotlin project.
    val sourceSets = project.property("sourceSets") as SourceSetContainer

    return listOf(SourceDirectorySetAndName("main", sourceSets.getByName("main").kotlin!!))
}

/**
 * 1. kotlin plugin can't fetch path of native. so, use same ios folder for native source
 * 2. we don't know if uses common native source set or not. so, add same source set to all native source sets
 */
fun Project.getNativeSourceSetOptions(): List<SourceDirectorySetAndName> {
    return extensions.findByType(KotlinMultiplatformExtension::class.java)?.let { ext ->
        ext.targets
            .filter { it.platformType == KotlinPlatformType.native }
            .flatMap { it.compilations }
            .map {
                println("native compilation : ${it.compilationName}, ${it.defaultSourceSet.name}")
                SourceDirectorySetAndName(NATIVE_TARGET_NAME, it.defaultSourceSet.kotlin)
            }
    } ?: emptyList()
}

fun SourceDirectorySetAndName.addGeneratedSourceDirectory(project: Project) {
    //todo name is fine?
    println("source set name : $name")
    sourceDirectorySet.srcDir("${project.buildDir}/generated/source/simpleapi/$name")
}

internal val AndroidSourceSet.kotlin: SourceDirectorySet?
    get() = kotlinSourceSet

internal val SourceSet.kotlin: SourceDirectorySet?
    get() = kotlinSourceSet

private val Any.kotlinSourceSet: SourceDirectorySet?
    get() = (getKotlinSourceSet(KOTLIN_DSL_NAME) ?: getKotlinSourceSet(KOTLIN_JS_DSL_NAME))
        ?.kotlin

/**
 * `kotlinPlugin.javaClass.interfaces` has only 'org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet'
 * `convention.plugins` has only 'kotlin'
 */
private fun Any.getKotlinSourceSet(name: String): KotlinSourceSet? =
    (this as HasConvention).convention.plugins[name] as? KotlinSourceSet?


//todo use one code. instead of duplication
open class SourceSetOption(val name: String, val sourcePathSet: Set<String>)
data class SourceDirectorySetAndName(val name: String, val sourceDirectorySet: SourceDirectorySet)

fun SourceDirectorySetAndName.toOption(): SourceSetOption =
    SourceSetOption(name, sourceDirectorySet.sourceDirectories.map { it.absolutePath }.toSet())

