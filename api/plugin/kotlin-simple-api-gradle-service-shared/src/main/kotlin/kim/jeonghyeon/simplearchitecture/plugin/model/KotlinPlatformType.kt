package kim.jeonghyeon.simplearchitecture.plugin.model

/**
 * Todo if build script kotlin version support 1.4.20 properly. change to library's class
 * org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType is not recognized on 1.3.72
 * and when build, the warning below is shown.
 * - The `embedded-kotlin` and `kotlin-dsl` plugins rely on features of Kotlin `1.3.72` that might work differently than in the requested version `1.4.20`
 * after that, error occurs. Named is used by KotlinPlatformType
 * - e: java.lang.NoClassDefFoundError: org/gradle/api/Named
 *
 *
 */
enum class KotlinPlatformType {
    common,jvm,js,androidJvm,native
}