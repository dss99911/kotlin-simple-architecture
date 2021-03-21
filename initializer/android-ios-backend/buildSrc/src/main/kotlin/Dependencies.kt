object versions {
    const val simpleArch = "1.4.60"

    object kotlin {
        const val version = "1.4.20"
    }

    object android {
        const val buildTool = "7.0.0-alpha09"
    }

    const val sqldelight = "1.4.4"

    const val shadow = "5.1.0"
}

object deps {
    object kotlin {
        val gradle = depKotlin("gradle-plugin", versions.kotlin.version)
        val serializationGradle = depKotlin("serialization", versions.kotlin.version)
    }

    object android {
        const val buildToolGradle = "com.android.tools.build:gradle:${versions.android.buildTool}"
    }

    object simpleArch {
        val client = depSimpleArchitecture("client", versions.simpleArch)
        val backend = depSimpleArchitecture("backend", versions.simpleArch)
        val gradle = depSimpleArchitecture("gradle", versions.simpleArch)
        val gradlePluginId = gradle.toPluginId()
    }

    object sqldelight {
        const val gradle = "com.squareup.sqldelight:gradle-plugin:${versions.sqldelight}"
    }

    const val shadowGradle = "com.github.jengelman.gradle.plugins:shadow:${versions.shadow}"

}

private fun depKotlin(module: String, version: String? = null): String =
    "org.jetbrains.kotlin:kotlin-$module${version?.let { ":$version" } ?: ""}"


private fun depKotlinx(module: String, version: String? = null): String =
    "org.jetbrains.kotlinx:kotlinx-$module${version?.let { ":$version" } ?: ""}"

private fun depSimpleArchitecture(module: String? = null, version: String? = null): String =
    "kim.jeonghyeon:kotlin-simple-architecture${module?.let { "-$module" } ?: ""}${version?.let { ":$version" } ?: ""}"

private fun String.toPluginId() = split(':')[0] + "." + split(':')[1]