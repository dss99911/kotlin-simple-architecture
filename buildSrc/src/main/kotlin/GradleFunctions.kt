import org.gradle.api.artifacts.dsl.DependencyHandler

fun DependencyHandler.apis(dependencyNotations: List<Any>) =
    dependencyNotations.forEach {
        add("api", it)
    }
