package kim.jeonghyeon.simplearchitecture.plugin.util

import org.gradle.api.Project

/**
 * when create db() and HttpClient.create().
 * it's created on each gradle module.
 * so, if there are multiple module. each module's package name of the generated file should be different.
 * so, made them different by module name.
 * but, if module names are same. there will be error
 */
fun Project.getGeneratedPackageName(): String {
    return name.filter { it != '-' && it != '_' }
}

fun Project.getGeneratedPackagePath(): String {
    return getGeneratedPackageName().replace(".", "/")
}