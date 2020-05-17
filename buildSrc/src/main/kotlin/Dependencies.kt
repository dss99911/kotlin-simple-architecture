object deps {
    object kotlin {
        val gradle = depKotlin("gradle-plugin", versions.kotlin.version)
        val serializationGradle = depKotlin("serialization", versions.kotlin.version)
        val stdlib = depKotlin("stdlib")
        val stdlibWithVersion = depKotlin("stdlib", versions.kotlin.version)
        val stdlibJdk8 = depKotlin("stdlib-jdk8", versions.kotlin.version)
        val coroutineCoreCommon = depKotlinx("coroutines-core-common", versions.kotlin.coroutine)
        val coroutineCore = depKotlinx("coroutines-core", versions.kotlin.coroutine)
        val coroutineAndroid = depKotlinx("coroutines-android", versions.kotlin.coroutine)
        val coroutineCoreNative = depKotlinx("coroutines-core-native", versions.kotlin.coroutine)
        val coroutineTest = depKotlinx("coroutines-test", versions.kotlin.coroutine)
        val serializationRuntimeCommon =
            depKotlinx("serialization-runtime-common", versions.kotlin.serialization)
        val serializationRuntime =
            depKotlinx("serialization-runtime", versions.kotlin.serialization)
        val serializationRuntimeNative =
            depKotlinx("serialization-runtime", versions.kotlin.serialization)
        val reflect = depKotlin("reflect", versions.kotlin.version)
        val testJunit = depKotlin("test-junit", versions.kotlin.version)
        val test = depKotlin("test", versions.kotlin.version)
    }

    object ktor {
        const val gson = "io.ktor:ktor-gson:${versions.kotlin.ktor}"
        const val serialization = "io.ktor:ktor-serialization:${versions.kotlin.ktor}"
        const val serverNetty = "io.ktor:ktor-server-netty:${versions.kotlin.ktor}"
        const val clientCore = "io.ktor:ktor-client-core:${versions.kotlin.ktor}"
        const val clientCoreJvm = "io.ktor:ktor-client-core-jvm:${versions.kotlin.ktor}"
        const val clientIos = "io.ktor:ktor-client-ios:${versions.kotlin.ktor}"
        const val clientAndroid = "io.ktor:ktor-client-android:${versions.kotlin.ktor}"
        const val clientGson = "io.ktor:ktor-client-gson:${versions.kotlin.ktor}"
        const val clientLogging = "io.ktor:ktor-client-logging:${versions.kotlin.ktor}"
        const val clientLoggingJvm = "io.ktor:ktor-client-logging-jvm:${versions.kotlin.ktor}"
        const val clientLoggingNative = "io.ktor:ktor-client-logging-native:${versions.kotlin.ktor}"
        const val clientSerialization = "io.ktor:ktor-client-serialization:${versions.kotlin.ktor}"
        const val clientSerializationJvm =
            "io.ktor:ktor-client-serialization-jvm:${versions.kotlin.ktor}"
        const val clientSerializationNative =
            "io.ktor:ktor-client-serialization-native:${versions.kotlin.ktor}"
    }

    object android {
        const val buildToolGradle = "com.android.tools.build:gradle:${versions.android.buildTool}"
        const val navigationGradle =
            "androidx.navigation:navigation-safe-args-gradle-plugin:${versions.android.xUi}"
        const val supportCompat = "com.android.support:support-compat:28.0.0"
        const val appCompat = "androidx.appcompat:appcompat:${versions.android.xBase}"
        const val core = "androidx.core:core-ktx:${versions.android.xBase}"
        const val vectordrawable =
            "androidx.vectordrawable:vectordrawable:${versions.android.xBase}"
        const val recyclerView = "androidx.recyclerview:recyclerview:1.1.0"
        const val material = "com.google.android.material:material:${versions.android.material}"
        const val preference = "androidx.preference:preference:1.1.0"
        const val fragment = "androidx.fragment:fragment-ktx:${versions.android.fragment}"
        const val fragmentTesting =
            "androidx.fragment:fragment-testing:${versions.android.fragment}"
        const val viewPager = "androidx.viewpager2:viewpager2:1.0.0"
        const val work = "androidx.work:work-runtime-ktx:2.3.1"
        const val paging = "androidx.paging:paging-runtime-ktx:2.1.1"
        const val constraintlayout =
            "androidx.constraintlayout:constraintlayout:${versions.android.constraintLayout}"
        val lifecycle = listOf(
            "androidx.lifecycle:lifecycle-extensions:${versions.android.xUi}",
            "androidx.lifecycle:lifecycle-livedata-ktx:${versions.android.xUi}",
            "androidx.lifecycle:lifecycle-viewmodel-ktx:${versions.android.xUi}",
            "androidx.lifecycle:lifecycle-common-java8:${versions.android.xUi}",
            "androidx.lifecycle:lifecycle-viewmodel-savedstate:${versions.android.xUi}"
        )
        val navigation = listOf(
            "androidx.navigation:navigation-fragment-ktx:${versions.android.xUi}",
            "androidx.navigation:navigation-ui-ktx:${versions.android.xUi}"
        )
        val room = listOf(
            "androidx.room:room-runtime:${versions.android.room}",
            "androidx.room:room-ktx:${versions.android.room}"
        )
        val coin = listOf(
            "org.koin:koin-core:${versions.koin}",
            "org.koin:koin-android:${versions.koin}",
            "org.koin:koin-androidx-scope:${versions.koin}",
            "org.koin:koin-androidx-viewmodel:${versions.koin}"
        )
        const val picasso = "com.squareup.picasso:picasso:2.71828"
        const val anko = "org.jetbrains.anko:anko:0.10.8"
        const val timber = "com.jakewharton.timber:timber:4.7.1"

        val testCommon = listOf(
            "androidx.arch.core:core-testing:2.1.0",
            "androidx.test.ext:junit-ktx:1.1.1",
            "androidx.test:core-ktx:${versions.android.xTest}",
            "androidx.test:core:${versions.android.xTest}",
            "androidx.test:rules:${versions.android.xTest}",
            "androidx.test:runner:${versions.android.xTest}",
            "org.mockito:mockito-core:3.2.4",
            kotlin.coroutineTest,
            "androidx.room:room-testing:${versions.android.room}",
            "com.google.truth:truth:1.0.1"
        )
    }

    object simpleArch {
        val common = depSimpleArchitecture(version = PublishConfig.versionLibrary)
        val jvm = depSimpleArchitecture("jvm", PublishConfig.versionLibrary)
        val android = depSimpleArchitecture("android", PublishConfig.versionLibrary)

        val androidTest = depSimpleArchitecture("android-test", PublishConfig.versionTest)
        val androidAndroidTest =
            depSimpleArchitecture("android-androidtest", PublishConfig.versionTest)

        val pluginShared =
            depSimpleArchitecture("gradle-plugin-api-shared", PublishConfig.versionLibrary)
        val pluginApi = depSimpleArchitecture("gradle-plugin-api", PublishConfig.versionLibrary)
        val pluginApiNative =
            depSimpleArchitecture("gradle-plugin-api-native", PublishConfig.versionLibrary)
        val pluginGradle = depSimpleArchitecture("gradle-plugin", PublishConfig.versionLibrary)
    }


    object bintrary {
        const val gradle = "com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.5"
    }

    object plugin {
        val gradleApi = depKotlin("gradle-plugin-api", versions.kotlin.version)
        val compilerEmbeddable = depKotlin("compiler-embeddable", versions.kotlin.version)
        val compiler = depKotlin("compiler", versions.kotlin.version)
        const val poet = "com.squareup:kotlinpoet:1.3.0"
        const val auto = "com.google.auto.service:auto-service:1.0-rc6"
    }

    const val shadowGradle = "com.github.jengelman.gradle.plugins:shadow:${versions.shadow}"
    const val gson = "com.google.code.gson:gson:2.8.6"
    const val logback = "ch.qos.logback:logback-classic:1.2.3"
    const val junit = "junit:junit:4.12"

}

private fun depKotlin(module: String, version: String? = null): String =
    "org.jetbrains.kotlin:kotlin-$module${version?.let { ":$version" } ?: ""}"


private fun depKotlinx(module: String, version: String? = null): String =
    "org.jetbrains.kotlinx:kotlinx-$module${version?.let { ":$version" } ?: ""}"

private fun depSimpleArchitecture(module: String? = null, version: String? = null): String =
    "kim.jeonghyeon:kotlin-simple-architecture${module?.let { "-$module" } ?: ""}${version?.let { ":$version" } ?: ""}"

fun String.getGroupId(): String = split(':')[0]
fun String.getArtifactId(): String = split(':')[1]
fun String.getVersion(): String = split(':')[2]