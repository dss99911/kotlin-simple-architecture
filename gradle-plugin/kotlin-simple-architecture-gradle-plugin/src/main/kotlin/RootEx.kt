import kim.jeonghyeon.simplearchitecture.plugin.SimpleArchExtension
import kim.jeonghyeon.simplearchitecture.plugin.util.simpleArchExtension
import org.gradle.api.Project

fun Project.simpleArch(dsl: SimpleArchExtension.() -> Unit) {
    dsl(simpleArchExtension ?: return)
}