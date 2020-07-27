import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

fun Project.publishMPP() {
    apply(from = "${rootDir.path}/publish/publishMPP.gradle")
}

fun Project.publishJvm() {
    apply(from = "${rootDir.path}/publish/publishJvm.gradle")
}

fun Project.publishShadowJar() {
    apply(from = "${rootDir.path}/publish/publishShadowJar.gradle")
}

fun Project.publishGradlePlugin() {
    apply(from = "${rootDir.path}/publish/publishGradlePlugin.gradle")
}