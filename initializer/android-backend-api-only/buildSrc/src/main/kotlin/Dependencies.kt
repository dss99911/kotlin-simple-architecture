object versions {
    const val simpleArch = "1.4.51"

    object kotlin {
        const val version = "1.4.20"
        const val ktor = "1.5.0"
    }

    object android {
        const val buildTool = "4.1.1"
    }

    const val shadow = "5.1.0"
}

object deps {
    object kotlin {
        val gradle = depKotlin("gradle-plugin", versions.kotlin.version)
    }

    object ktor {
        const val clientLogging = "io.ktor:ktor-client-logging:${versions.kotlin.ktor}"
        const val clientGson = "io.ktor:ktor-client-gson:${versions.kotlin.ktor}"
        const val gson = "io.ktor:ktor-gson:${versions.kotlin.ktor}"
        const val serverNetty = "io.ktor:ktor-server-netty:${versions.kotlin.ktor}"
    }

    object android {
        const val buildToolGradle = "com.android.tools.build:gradle:${versions.android.buildTool}"
    }

    object simpleApi {
        val client = depSimpleApi("client", versions.simpleArch)
        val backend = depSimpleApi("backend", versions.simpleArch)
        val gradle = depSimpleApi("gradle", versions.simpleArch)
        val gradlePluginId = gradle.toPluginId()
    }

    const val shadowGradle = "com.github.jengelman.gradle.plugins:shadow:${versions.shadow}"

}

private fun depKotlin(module: String, version: String? = null): String =
    "org.jetbrains.kotlin:kotlin-$module${version?.let { ":$version" } ?: ""}"


private fun depKotlinx(module: String, version: String? = null): String =
    "org.jetbrains.kotlinx:kotlinx-$module${version?.let { ":$version" } ?: ""}"

private fun depSimpleApi(module: String? = null, version: String? = null): String =
    "kim.jeonghyeon:kotlin-simple-api${module?.let { "-$module" } ?: ""}${version?.let { ":$version" } ?: ""}"

private fun String.toPluginId() = split(':')[0] + "." + split(':')[1]