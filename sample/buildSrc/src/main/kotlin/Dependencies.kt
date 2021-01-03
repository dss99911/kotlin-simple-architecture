object versions {
    const val simpleArch = "1.4.51"

    object kotlin {
        const val version = "1.4.20"
        const val ktor = "1.4.3"
    }

    object android {
        const val buildTool = "7.0.0-alpha02"
    }

    const val sqldelight = "1.4.2"

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

    object ktor {
        const val clientOkHttp = "io.ktor:ktor-client-okhttp:${versions.kotlin.ktor}"
        const val clientGson = "io.ktor:ktor-client-gson:${versions.kotlin.ktor}"
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