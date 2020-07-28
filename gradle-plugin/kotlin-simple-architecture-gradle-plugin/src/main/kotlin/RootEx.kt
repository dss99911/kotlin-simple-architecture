import kim.jeonghyeon.simplearchitecture.plugin.extension.SimpleArchExtension
import kim.jeonghyeon.simplearchitecture.plugin.extension.simpleArchExtension
import org.gradle.api.Project

fun Project.simpleArch(dsl: SimpleArchExtension.() -> Unit) {
    dsl(simpleArchExtension ?: return)
}