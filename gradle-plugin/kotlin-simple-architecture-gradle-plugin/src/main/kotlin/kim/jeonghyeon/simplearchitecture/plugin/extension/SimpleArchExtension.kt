package kim.jeonghyeon.simplearchitecture.plugin.extension

import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionContainer

open class SimpleArchExtension {
    var postfix: String = ""
    var simpleConfig: Boolean = true//use on base module only
    var androidConfig: Boolean = true
    var commonConfig: Boolean = true
    var generationConfig: Boolean = true
}

val Project.simpleArchExtension get() = project.extensions.findOrCreate("simpleArch", SimpleArchExtension::class.java)

fun <T> ExtensionContainer.findOrCreate(name:String, clazz: Class<T>): T {
    var ext = findByType(clazz)
    if (ext == null) {
        ext = create(name, clazz)!!
    }
    return ext
}