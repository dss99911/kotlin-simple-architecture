import PublishConfig.publishModeLibrary
import PublishConfig.publishModeTest
import PublishConfig.versionLibraryForPublish
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

/**
 * Step to publish
 * Todo : consider to make task or shell script
 * 0. Let's say current version is 0.2
 * 1. [publishModeLibrary]=true, [publishModeTest]=false. increase [versionLibraryForPublish] to 0.3 //todo android library depends on jvm library. so, should be released after android library
 * 2. build android library => ./gradlew bintrayUpload
 * 3. publishModeLibrary=false, publishModeTest=true. increase test publish version to 0.3 and library version to 0.3
 * 4. build testing, androidTesting module => ./gradlew bintrayUpload
 * 5. increase test version to 3.0
 * 6. https://bintray.com/hyun/kotlin-simple-architecture
 */
object PublishConfig {
    val publishModeLibrary = false
    val publishModeTest = true

    val versionLibrary = "0.4"
    val versionLibraryForPublish = "0.4"

    val versionTest = "0.4"
    val versionTestForPublish = "0.4"
}

fun Project.publish(isTest: Boolean, isAndroid: Boolean, artifactId: String, artifactName: String) {
    if ((PublishConfig.publishModeTest && isTest)
        || (PublishConfig.publishModeLibrary && !isTest)) {

        extensions.apply {
            add("artifact_id", artifactId)
            add("artifact_name", artifactName)
            add("publish_version", if (isTest) PublishConfig.versionTestForPublish else PublishConfig.versionLibraryForPublish)
        }

        apply(from = if (isAndroid) "../publishAndroid.gradle" else "../publishJvm.gradle")
    }
}