package kim.jeonghyeon.simplearchitecture.plugin.util

import org.gradle.api.Project

fun Project.getPackageName(): String {
    return "$group.$name".filter { it != '-' && it != '_' }
}

fun Project.getPackagePath(): String {
    return getPackageName().replace(".", "/")
}