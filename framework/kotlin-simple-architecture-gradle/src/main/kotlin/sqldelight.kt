import com.squareup.sqldelight.gradle.SqlDelightExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

//default function is not working when using apply. if use plugins { id() }, then it's working.
fun Project.sqldelight(dsl: SqlDelightExtension.() -> Unit) {
    dsl(extensions.getByType())
}