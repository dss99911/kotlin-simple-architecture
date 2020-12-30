import com.squareup.sqldelight.gradle.SqlDelightExtension
import kim.jeonghyeon.simplearchitecture.plugin.extension.SimpleArchExtension
import kim.jeonghyeon.simplearchitecture.plugin.extension.simpleArchExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

fun Project.simpleArch(dsl: SimpleArchExtension.() -> Unit) {
    dsl(simpleArchExtension)
}

//default function is not working when using apply. if use plugins { id() }, then it's working.
fun Project.sqldelight(dsl: SqlDelightExtension.() -> Unit) {
    dsl(extensions.getByType())
}