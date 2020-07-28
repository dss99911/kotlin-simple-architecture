package kim.jeonghyeon.simplearchitecture.plugin.extension

import org.gradle.api.Project

open class SimpleArchExtension {
    var postfix: String = ""
    var simpleConfig: Boolean = true//use on base module only
    var androidConfig: Boolean = true
    var commonConfig: Boolean = true
    var generationConfig: Boolean = true
}

val Project.simpleArchExtension get() = project.extensions.getByType(SimpleArchExtension::class.java)